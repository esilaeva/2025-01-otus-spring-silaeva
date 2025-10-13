# Minikube Services Deployment Guide

This guide provides step-by-step instructions for deploying your MySQL database and library application in Minikube.

## Prerequisites

Before starting, ensure you have the following tools installed:

- **Minikube**: A tool for running Kubernetes locally
- **kubectl**: The Kubernetes command-line interface
- **Docker**: Required for building container images

### Verify Installation

```bash
# Check Minikube version
minikube version

# Check kubectl version
kubectl version --client

# Check Docker version
docker version
```

## Step 1: Start and Configure Minikube

### Start Minikube Cluster

```bash
# Start Minikube with recommended resources for database applications
minikube start --memory=4096 --cpus=2 --disk-size=10g

# Verify cluster status
minikube status

# Check cluster nodes
kubectl get nodes
```

### Configure Docker Environment (Important)

To build and use your application image locally, you need to configure your Docker environment to use Minikube's Docker daemon:

```bash
# Configure your shell to use Minikube's Docker daemon
eval $(minikube docker-env)

# Verify you're using Minikube's Docker
docker ps
```

**Note**: This command must be run in every new terminal session where you want to build Docker images for Minikube.

## Step 2: Build Application Image

Before deploying your application, you need to build the Docker image inside Minikube's environment:

```bash
# Make sure you're in the project directory
cd /home/megazoid/OTUS/hw17-docker-mysql

# Configure Docker environment for current session
eval $(minikube docker-env)

# Build your application image (adjust the build command as needed)
docker build -t library-service:1.0 .

# Verify the image was built
docker images | grep library-service
```

## Step 3: Deploy MySQL Database

### Deploy MySQL Components

```bash
# Apply the MySQL deployment manifest
kubectl apply -f mysql-deployment.yaml

# Verify MySQL deployment
kubectl get deployments
kubectl get pods -l app=library-db
kubectl get services -l app=library-db
kubectl get pvc
```

### Check MySQL Deployment Status

```bash
# Monitor deployment progress
kubectl rollout status deployment/library-db

# Check if MySQL pod is running
kubectl get pods -l app=library-db -w
```

### Verify MySQL Service

```bash
# Check service details
kubectl describe service library-db

# Verify persistent volume claim
kubectl describe pvc mysql-pv-claim
```

## Step 4: Deploy Library Application

### Deploy Application Components

```bash
# Apply the application deployment manifest
kubectl apply -f app-deployment.yaml

# Verify application deployment
kubectl get deployments
kubectl get pods -l app=library
kubectl get services -l app=library
```

### Check Application Deployment Status

```bash
# Monitor deployment progress
kubectl rollout status deployment/library

# Check if application pod is running
kubectl get pods -l app=library -w
```

## Step 5: Access Your Applications

### List All Services

```bash
# View all services in the cluster
kubectl get services

# Use Minikube to list service URLs
minikube service list
```

### Access Library Application

```bash
# Get the NodePort service URL
minikube service library-service --url

# Open the application in your browser
minikube service library-service

# Or access via curl (replace with actual URL from above command)
curl $(minikube service library-service --url)
```

### Access MySQL Database (For Testing)

```bash
# Port-forward to MySQL for direct access (optional)
kubectl port-forward service/library-db 3306:3306

# In another terminal, connect using MySQL client
mysql -h localhost -P 3306 -u mysql -pmysql db
```

## Step 6: Verify Deployment

### Check Pod Logs

```bash
# Check MySQL logs
kubectl logs -l app=library-db

# Check application logs
kubectl logs -l app=library

# Follow logs in real-time
kubectl logs -f -l app=library
```

### Test Connectivity Between Services

```bash
# Execute a shell in the application pod
kubectl exec -it $(kubectl get pod -l app=library -o jsonpath='{.items[0].metadata.name}') -- /bin/bash

# Inside the pod, test MySQL connection
# Example using netcat or telnet if available:
# nc -zv library-db 3306
```

## Step 7: Scaling and Management

### Scale Application

```bash
# Scale the application to 3 replicas
kubectl scale deployment library --replicas=3

# Verify scaling
kubectl get pods -l app=library

# Scale back to 1 replica
kubectl scale deployment library --replicas=1
```

### Update Application

```bash
# After rebuilding your image with a new tag
eval $(minikube docker-env)
docker build -t library-service:2.0 .

# Update the deployment with the new image
kubectl set image deployment/library library=library-service:2.0

# Check rollout status
kubectl rollout status deployment/library

# Rollback if needed
kubectl rollout undo deployment/library
```

## Monitoring and Troubleshooting

### Common Commands

```bash
# Get all resources
kubectl get all

# Describe a specific resource for detailed information
kubectl describe pod <pod-name>
kubectl describe service <service-name>
kubectl describe deployment <deployment-name>

# Check cluster events
kubectl get events --sort-by=.metadata.creationTimestamp

# Check resource usage
kubectl top nodes
kubectl top pods
```

### Accessing Kubernetes Dashboard (Optional)

```bash
# Enable dashboard addon
minikube addons enable dashboard

# Open dashboard in browser
minikube dashboard
```

## Cleanup

### Remove Deployments

```bash
# Delete application components
kubectl delete -f app-deployment.yaml

# Delete MySQL components
kubectl delete -f mysql-deployment.yaml

# Verify cleanup
kubectl get all
```

### Stop Minikube

```bash
# Stop the cluster
minikube stop

# Delete the cluster (removes all data)
minikube delete
```

## Troubleshooting Tips

1. **ImagePullBackOff Error**: Make sure you've built the image using Minikube's Docker daemon (`eval $(minikube docker-env)`)

2. **Service Not Accessible**: 
   - Check if pods are running: `kubectl get pods`
   - Verify service endpoints: `kubectl get endpoints`
   - Use `minikube service <service-name> --url` to get the correct URL

3. **MySQL Connection Issues**:
   - Check if MySQL pod is ready: `kubectl get pods -l app=library-db`
   - Verify service is created: `kubectl get svc library-db`
   - Check logs: `kubectl logs -l app=library-db`

4. **Persistent Storage Issues**:
   - Check PVC status: `kubectl get pvc`
   - Ensure storage class is available: `kubectl get storageclass`

5. **Resource Constraints**:
   - Check node resources: `kubectl top nodes`
   - Increase Minikube memory/CPU if needed: `minikube start --memory=8192 --cpus=4`

## Additional Resources

- [Minikube Documentation](https://minikube.sigs.k8s.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Kubernetes Troubleshooting](https://kubernetes.io/docs/tasks/debug-application-cluster/)

## Application Architecture

Your deployment includes:

- **MySQL Database** (`library-db`):
  - Persistent storage using PVC
  - Headless service for direct pod access
  - Environment variables for database configuration

- **Library Application** (`library`):
  - Spring Boot application connecting to MySQL
  - NodePort service for external access
  - Database connection configured via environment variables

The applications communicate using Kubernetes internal DNS (`library-db:3306`).
