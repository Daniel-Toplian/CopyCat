# CopyCat

CopyCat is a flexible API mock service that helps developers simulate RESTful APIs without needing a live backend or client.  
It allows easy creation, configuration, and testing of mock services, making it useful for local development and continuous integration environments.  

## Building and Running with Docker

To build and run the CopyCat service using Docker:

1. **Build the Docker image:**

    ```bash
    docker build -t copycat .
    ```

2. **Run the Docker container:**

    ```bash
    docker-compose up
    ```

   This command will launch CopyCat along with the MongoDB service (if configured to use MongoDB for recovery).

## Environment Variables

The following environment variables are used to configure the project:

| Variable                | Description                                       | Default Value |
|-------------------------|---------------------------------------------------|---------------|
| `EXPOSED_PORT`          | The port on which the server runs                 | `8080`        |
| `RECOVERY_TYPE`         | Defines the recovery method (`file` or `mongodb`) | `file`        |
| `MONGODB_AUTH_DATABASE` | MongoDB authentication database                   | `admin`       |
| `MONGODB_USERNAME`      | MongoDB username                                  | `root`        |
| `MONGODB_PASSWORD`      | MongoDB password                                  | `root`        |
| `MONGODB_PORT`          | MongoDB service port                              | `27017`       |
| `MONGODB_HOST`          | MongoDB service host                              | `localhost`   |
| `MONGODB_DATABASE`      | Name of the MongoDB database                      | `copycat`     |

## API Usage

For details on how to use the API, refer to the [API Usage Documentation](./api-usage.md).
