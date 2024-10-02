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
	genApacheClient := flags.Bool("gen-apache-client", false, "Generate Apache client")
	opts := &protogen.Options{
		ParamFunc: flags.Set,
	}
	opts.Run(func(plugin *protogen.Plugin) error {
		g, err := generator.New(generator.Options{
			GenerateHelidonClient: *genHelidonClient,
			GenerateHelidonServer: *genHelidonServer,
			GenerateApacheClient:  *genApacheClient,
		})
		if err != nil {
			return err
		}
		return g.Generate(plugin)
	})
}
