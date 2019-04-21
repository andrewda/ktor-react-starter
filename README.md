# ktor-react-starter

This is a base starter application for Ktor + React.

## Running

To run the program, use the commands below:

```bash
$ ./gradlew build         # builds the frontend/backend
$ ./gradlew deployStatic  # deploys generated frontend
$ ./gradlew :backend:run  # runs the backend server
```

After starting the backend, you can visit the website at `http://127.0.0.1:8080`.

If you're working on the frontend and would prefer a hot-reloading instance, you can use:

```bash
./gradlew :frontend:run
```

Then you can reach the frontend at `http://127.0.0.1:3000`.
