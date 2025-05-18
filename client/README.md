# Client

## Usage

Install dependencies
```bash
pnpm install
```

Run the app
```bash
pnpm dev
```

Build the app
```bash
pnpm build
```

Build the docker image
```bash
docker build -t client-image client
```

Run the docker image
```bash
docker run -p 3000:80 client-image
```

## Additional Commands

Formatting
```bash
pnpm format
pnpm format:check
```

Linting
```bash
pnpm lint
```
