package generator

import (
	"fmt"
	"os"
	"strings"
)

type JavaPackage string

type JavaMethod struct {
	Name       string
	ReturnType string
	Arguments  []JavaMethodArgument
	Throws     []string
}

type JavaMethodArgument struct {
	Name string
	Type string
}

func (jp JavaPackage) Resolve(cn JavaClassName) JavaClassName {
	if jp != "" {
		return JavaClassName(string(jp) + "." + string(cn))
	} else {
		return cn
	}
}

func (jp JavaPackage) Path() string {
	return strings.ReplaceAll(string(jp), ".", string(os.PathSeparator))
}

type JavaClassName string

func (cn JavaClassName) Path() string {
	return strings.ReplaceAll(string(cn), ".", string(os.PathSeparator)) + ".java"
}

func (m JavaMethod) String() string {
	var args []string
	for _, arg := range m.Arguments {
		args = append(args, fmt.Sprintf("%s %s", arg.Type, arg.Name))
	}
	s := fmt.Sprintf("%s %s(%s)", m.ReturnType, m.Name, strings.Join(args, ", "))
	if len(m.Throws) > 0 {
		s = s + " throws " + strings.Join(m.Throws, ", ")
	}
	return s
}
