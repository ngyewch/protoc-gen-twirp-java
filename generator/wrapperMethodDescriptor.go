package generator

import (
	"github.com/iancoleman/strcase"
	"google.golang.org/protobuf/reflect/protoreflect"
)

type MethodDescriptorWrapper struct {
	md         protoreflect.MethodDescriptor
	twirpPath  string
	javaMethod JavaMethod
}

func WrapMethodDescriptor(md protoreflect.MethodDescriptor) MethodDescriptorWrapper {
	sd, ok := md.Parent().(protoreflect.ServiceDescriptor)
	if !ok {
		panic("expected ServiceDescriptor not found")
	}
	fd, ok := sd.Parent().(protoreflect.FileDescriptor)
	if !ok {
		panic("expected FileDescriptor not found")
	}
	twirpPath := "/"
	if fd.Package() != "" {
		twirpPath += string(fd.Package()) + "."
	}
	twirpPath += string(sd.Name()) + "/" + string(md.Name())

	return MethodDescriptorWrapper{
		md:        md,
		twirpPath: twirpPath,
		javaMethod: JavaMethod{
			Name:       strcase.ToLowerCamel(string(md.Name())),
			ReturnType: messageDescriptorToJavaType(md.Output()),
			Arguments: []JavaMethodArgument{
				{
					Type: messageDescriptorToJavaType(md.Input()),
					Name: "input",
				},
			},
		},
	}
}

func (w MethodDescriptorWrapper) Name() string {
	return string(w.md.Name())
}

func (w MethodDescriptorWrapper) JavaMethod() JavaMethod {
	return w.javaMethod
}

func (w MethodDescriptorWrapper) TwirpPath() string {
	return w.twirpPath
}

func messageDescriptorToJavaType(md protoreflect.MessageDescriptor) string {
	fdw := WrapFileDescriptor(md.ParentFile(), false)
	jc := JavaClassName(strcase.ToCamel(string(md.Name())))
	return string(fdw.JavaPackage().Resolve(jc))
}
