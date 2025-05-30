FROM node:24-alpine AS build

ARG VITE_GH_OAUTH_CLIENT_ID
ARG VITE_GH_CONNECTOR_URL
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN --mount=type=cache,id=pnpm,target=/root/.local/share/pnpm/store pnpm fetch --frozen-lockfile
RUN --mount=type=cache,id=pnpm,target=/root/.local/share/pnpm/store pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

# Use an Nginx image to serve the static files
FROM nginx:alpine

COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE ${CLIENT_PORT:-80}

CMD ["nginx", "-g", "daemon off;"]
