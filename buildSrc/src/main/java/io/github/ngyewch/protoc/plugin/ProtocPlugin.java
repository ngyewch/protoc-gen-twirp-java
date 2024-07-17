package io.github.ngyewch.protoc.plugin;

import java.io.File;
import javax.inject.Inject;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;

public class ProtocPlugin implements Plugin<Project> {
  private static final BuildParameters[] BUILD_PARAMETERS_LIST = {
    new BuildParameters("linux", "arm64", "linux-aarch_64"),
    new BuildParameters("linux", "ppc64le", "linux-ppcle_64"),
    new BuildParameters("linux", "s390x", "linux-s390_64"),
    new BuildParameters("linux", "386", "linux-x86_32"),
    new BuildParameters("linux", "amd64", "linux-x86_64"),
    new BuildParameters("darwin", "arm64", "osx-aarch_64"),
    new BuildParameters("darwin", "amd64", "osx-x86_64"),
    new BuildParameters("windows", "386", "windows-x86_32"),
    new BuildParameters("windows", "amd64", "windows-x86_64"),
  };

  private final SoftwareComponentFactory softwareComponentFactory;

  @Inject
  public ProtocPlugin(SoftwareComponentFactory softwareComponentFactory) {
    super();

    this.softwareComponentFactory = softwareComponentFactory;
  }

  @Override
  public void apply(Project project) {
    final Attribute<String> typeAttribute = Attribute.of("type", String.class);
    final Configuration configuration =
        project
            .getConfigurations()
            .create("binaries", c -> c.getAttributes().attribute(typeAttribute, "default"));

    final AdhocComponentWithVariants adhocComponent =
        softwareComponentFactory.adhoc("protocPlugin");
    project.getComponents().add(adhocComponent);
    adhocComponent.addVariantsFromConfiguration(
        configuration, ConfigurationVariantDetails::mapToOptional);

    final File buildDirectory = project.getLayout().getBuildDirectory().get().getAsFile();
    final File exeOutputDirectory = new File(buildDirectory, "exe");

    final TaskProvider<Task> buildBinariesTaskProvider =
        project.getTasks().register("buildBinaries");
    for (final BuildParameters buildParameters : BUILD_PARAMETERS_LIST) {
      final String taskName =
          String.format("build_%s_%s", buildParameters.getGoos(), buildParameters.getGoarch());
      final File outputExeFile =
          new File(
              exeOutputDirectory,
              String.format(
                  "%s-%s-%s.exe",
                  project.getName(), project.getVersion(), buildParameters.getClassifier()));
      final File outputAscFile =
          new File(
              exeOutputDirectory,
              String.format(
                  "%s-%s-%s.exe.asc",
                  project.getName(), project.getVersion(), buildParameters.getClassifier()));
      project
          .getArtifacts()
          .add(
              "binaries",
              outputExeFile,
              artifact -> {
                artifact.builtBy(taskName);
                artifact.setClassifier(buildParameters.getClassifier());
                artifact.setExtension("exe");
              });
      project
          .getArtifacts()
          .add(
              "binaries",
              outputExeFile,
              artifact -> {
                artifact.builtBy(taskName);
                artifact.setClassifier(buildParameters.getClassifier() + ".exe");
                artifact.setExtension("asc");
              });

      final TaskProvider<Exec> buildBinaryTaskProvider =
          project
              .getTasks()
              .register(
                  taskName,
                  Exec.class,
                  task -> {
                    task.doFirst(
                        t -> {
                          outputExeFile.getParentFile().mkdirs();
                          outputAscFile.getParentFile().mkdirs();
                        });
                    task.getOutputs().file(outputExeFile);
                    task.commandLine("go", "build", "-o", outputExeFile);
                    task.environment("GOOS", buildParameters.getGoos());
                    task.environment("GOARCH", buildParameters.getGoarch());
                  });

      buildBinariesTaskProvider.configure(task -> task.dependsOn(buildBinaryTaskProvider));
    }

    project
        .getTasks()
        .withType(
            PublishToMavenRepository.class, task -> task.dependsOn(buildBinariesTaskProvider));
  }
}
