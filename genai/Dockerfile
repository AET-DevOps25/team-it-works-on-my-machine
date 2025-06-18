# Use Python 3.12 slim as the base image
FROM python:3.12-slim
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

# Set the working directory to /app
WORKDIR /app

# Copy dependency file requirements.txt into the container
COPY pyproject.toml .

# Install Python dependencies
RUN --mount=type=cache,target=/root/.cache/uv \
    --mount=type=bind,source=uv.lock,target=uv.lock \
    --mount=type=bind,source=pyproject.toml,target=pyproject.toml \
    uv sync --locked --no-install-project

# Copy all project files into the container
COPY . .

# Sync the project
RUN --mount=type=cache,target=/root/.cache/uv \
    uv sync --locked

# Expose port
EXPOSE 3001

# Start the FastAPI app with uvicorn
CMD ["uv", "run", "app"]
