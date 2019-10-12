Json Schema Validator
=====================

An http server to validate json documents against a schema.

## Run the server

You can launch the server with sbt. The server will listen on port 8080:

    sbt run

## Endpoints

    POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
    GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`

    POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`

## Examples

#### Create a new schema

    curl -XPOST http://localhost:8080/schema/my-schema -d '
      {
        "$schema": "http://json-schema.org/draft-04/schema#",
        "type": "object",
        "properties": {
          "source": {
            "type": "string"
          },
          "destination": {
            "type": "string"
          },
          "timeout": {
            "type": "integer",
            "minimum": 0,
            "maximum": 32767
          },
          "chunks": {
            "type": "object",
            "properties": {
              "size": {
            "type": "integer"
              },
              "number": {
            "type": "integer"
              }
            },
            "required": ["size"]
          }
        },
        "required": ["source", "destination"]
      }'

#### Check your schema exists

    curl http://localhost:8080/schema/my-schema

#### Validate a document against your schema

    curl -XPOST http://localhost:8080/validate/my-schema -d '
      {
        "source": "/home/alice/image.iso",
        "destination": "/mnt/storage",
        "timeout": null,
        "chunks": {
          "size": 1024,
          "number": null
        }
      }
    '
