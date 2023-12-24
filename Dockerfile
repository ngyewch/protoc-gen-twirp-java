FROM debian:bookworm

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install -y curl unzip

RUN mkdir -p /opt/downloads

ENV PROTOC_VERSION=25.1
WORKDIR /opt/downloads
RUN curl -LO https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/protoc-${PROTOC_VERSION}-linux-x86_64.zip
RUN unzip protoc-${PROTOC_VERSION}-linux-x86_64.zip -d /opt/protoc-${PROTOC_VERSION}
ENV PATH=/opt/protoc-${PROTOC_VERSION}/bin:$PATH

ENV GO_VERSION=1.21.5
WORKDIR /opt/downloads
RUN curl -LO https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz
RUN tar -C /opt -xzf go${GO_VERSION}.linux-amd64.tar.gz
ENV PATH=/opt/go/bin:$PATH

WORKDIR /workspace
COPY . .
RUN go mod download
RUN go build -o /usr/local/bin/protoc-gen-twirp-java .
