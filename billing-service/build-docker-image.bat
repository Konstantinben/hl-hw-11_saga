REM docker build -t kpolyanichko/hl-hw-11-billing-service-amd64:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/hl-hw-11-billing-service-amd64:latest --push .
