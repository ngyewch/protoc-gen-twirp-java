package main

import (
	"flag"
	"github.com/ngyewch/protoc-gen-twirp-java/generator"
	"google.golang.org/protobuf/compiler/protogen"
)

func main() {
	var flags flag.FlagSet
	genHelidonClient := flags.Bool("gen-helidon-client", false, "Generate Helidon client")
	genHelidonServer := flags.Bool("gen-helidon-server", false, "Generate Helidon server")
	opts := &protogen.Options{
		ParamFunc: flags.Set,
	}
	opts.Run(func(plugin *protogen.Plugin) error {
		g, err := generator.New(generator.Options{
			GenerateHelidonClient: *genHelidonClient,
			GenerateHelidonServer: *genHelidonServer,
		})
		if err != nil {
			return err
		}
		return g.Generate(plugin)
	})
}
