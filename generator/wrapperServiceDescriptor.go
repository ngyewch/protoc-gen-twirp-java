package generator

import (
	"github.com/iancoleman/strcase"
	"google.golang.org/protobuf/reflect/protoreflect"
)

type ServiceDescriptorWrapper struct {
	sd            protoreflect.ServiceDescriptor
	javaClassName JavaClassName
	methods       []MethodDescriptorWrapper
}

func WrapServiceDescriptor(sd protoreflect.ServiceDescriptor) ServiceDescriptorWrapper {
	var methods []MethodDescriptorWrapper
	for i := 0; i < sd.Methods().Len(); i++ {
		method := sd.Methods().Get(i)
		methods = append(methods, WrapMethodDescriptor(method))
	}
	return ServiceDescriptorWrapper{
		sd:            sd,
		javaClassName: JavaClassName(strcase.ToCamel(string(sd.Name()))),
		methods:       methods,
	}
}

func (w ServiceDescriptorWrapper) Name() string {
	return string(w.sd.Name())
}

func (w ServiceDescriptorWrapper) JavaClassName() JavaClassName {
	return w.javaClassName
}

func (w ServiceDescriptorWrapper) Methods() []MethodDescriptorWrapper {
	return w.methods
}
