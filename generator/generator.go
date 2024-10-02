package generator

import (
	"bytes"
	"google.golang.org/protobuf/compiler/protogen"
	"text/template"
)

type Generator struct {
	options   Options
	templates *template.Template
}

type Options struct {
	GenerateHelidonClient bool
	GenerateHelidonServer bool
	GenerateApacheClient  bool
}

type TemplateData struct {
	Options            Options
	ProtoFile          FileDescriptorWrapper
	JavaOuterClassName string
}

func New(options Options) (*Generator, error) {
	templates, err := template.ParseFS(templateFS, "templates/*.tmpl")
	if err != nil {
		return nil, err
	}

	return &Generator{
		options:   options,
		templates: templates,
	}, nil
}

func (g *Generator) Generate(gen *protogen.Plugin) error {
	for _, f := range gen.Files {
		if !f.Generate {
			continue
		}

		if len(f.Services) == 0 {
			continue
		}

		fdw := WrapFileDescriptor(f.Desc, true)

		templateData := TemplateData{
			Options:   g.options,
			ProtoFile: fdw,
		}
		templateData.JavaOuterClassName = string(fdw.JavaOuterClassName()) + "Twirp"

		outputBuffer := bytes.NewBuffer(nil)
		err := g.templates.ExecuteTemplate(outputBuffer, "Twirp.java.tmpl", templateData)
		if err != nil {
			return err
		}

		fullOuterClassName := JavaClassName(string(fdw.JavaFullOuterClassName()) + "Twirp")
		generatedFile := gen.NewGeneratedFile(fullOuterClassName.Path(), f.GoImportPath)
		_, err = generatedFile.Write(outputBuffer.Bytes())
		if err != nil {
			return err
		}
	}
	return nil
}
