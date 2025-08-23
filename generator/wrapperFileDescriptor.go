package generator

import (
	"path/filepath"
	"strings"

	"github.com/iancoleman/strcase"
	"google.golang.org/protobuf/reflect/protoreflect"
	"google.golang.org/protobuf/types/descriptorpb"
)

type FileDescriptorWrapper struct {
	fd                 protoreflect.FileDescriptor
	javaPackage        JavaPackage
	javaOuterClassName JavaClassName
	javaMultipleFiles  bool
	services           []ServiceDescriptorWrapper
}

func WrapFileDescriptor(fd protoreflect.FileDescriptor, includeServices bool) FileDescriptorWrapper {
	javaPackage := string(fd.Package())
	javaOuterClassName := filenameToJavaClassName(fd.Path())
	javaMultipleFiles := false
	options, ok := fd.Options().(*descriptorpb.FileOptions)
	if ok {
		javaPackage1 := options.GetJavaPackage()
		if javaPackage1 != "" {
			javaPackage = javaPackage1
		}
		javaOuterClassName1 := options.GetJavaOuterClassname()
		if javaOuterClassName1 != "" {
			javaOuterClassName = JavaClassName(javaOuterClassName1)
		}
		javaMultipleFiles = options.GetJavaMultipleFiles()
	}

	var services []ServiceDescriptorWrapper
	if includeServices {
		for i := 0; i < fd.Services().Len(); i++ {
			service := fd.Services().Get(i)
			services = append(services, WrapServiceDescriptor(service))
		}
	}

	return FileDescriptorWrapper{
		fd:                 fd,
		javaPackage:        JavaPackage(javaPackage),
		javaOuterClassName: javaOuterClassName,
		javaMultipleFiles:  javaMultipleFiles,
		services:           services,
	}
}

func (w FileDescriptorWrapper) Descriptor() protoreflect.FileDescriptor {
	return w.fd
}

func (w FileDescriptorWrapper) JavaPackage() JavaPackage {
	return w.javaPackage
}

func (w FileDescriptorWrapper) JavaOuterClassName() JavaClassName {
	return w.javaOuterClassName
}

func (w FileDescriptorWrapper) JavaFullOuterClassName() JavaClassName {
	return w.javaPackage.Resolve(w.javaOuterClassName)
}

func (w FileDescriptorWrapper) Services() []ServiceDescriptorWrapper {
	return w.services
}

func filenameToJavaClassName(path string) JavaClassName {
	baseName := filepath.Base(path)
	if strings.HasSuffix(baseName, ".proto") {
		return JavaClassName(strcase.ToCamel(baseName[0 : len(baseName)-6]))
	} else {
		return JavaClassName(strcase.ToCamel(baseName))
	}
}
