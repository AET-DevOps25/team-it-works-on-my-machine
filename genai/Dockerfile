# Use Python 3.9 slim as the base image
FROM python:3.9-slim

# Set the working directory to /app
WORKDIR /app

# Copy dependency file requirements.txt into the container
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy all project files into the container
COPY . .

# Expose port 8000
EXPOSE 8000

# Start the FastAPI app with uvicorn, listening on 0.0.0.0:8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]