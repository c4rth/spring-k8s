# Overview

Repository to reproduce the errors when starting a Spring Boot app in k8s with combinations of:

- SPRING_CONFIG_IMPORT
- SPRING_CLOUD_BOOTSTRAP_ENABLED
- spring-cloud-starter-bootstrap

# Tests

__*Location of properties and dependency:*__

./k8s/k8s-deploy.yaml: SPRING_CONFIG_IMPORT and  SPRING_CLOUD_BOOTSTRAP_ENABLED

build.gradle.kts: implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

## Results

| SPRING_CONFIG_IMPORT | SPRING_CLOUD_BOOTSTRAP_ENABLED | spring-cloud-starter-bootstrap | result  |
|----------------------|--------------------------------|--------------------------------|---------|
| -                    | -                              | -                              | OK      |
| kubernetes:          | -                              | -                              | OK      |
| -                    | true                           | -                              | OK      |
| kubernetes:          | true                           | -                              | NOT OK* |
| -                    | -                              | added                          | OK      |
| -                    | true                           | added                          | OK      |
| kubernetes:          | -                              | added                          | NOT OK* |

### When NOT OK
```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of method configMapPropertySourceLocator in org.springframework.cloud.kubernetes.fabric8.config.Fabric8BootstrapConfiguration required a single bean, but 2 were found:
	- spring.cloud.kubernetes.config-org.springframework.cloud.kubernetes.commons.config.ConfigMapConfigProperties: defined in unknown location
	- configDataConfigMapConfigProperties: a programmatically registered singleton

org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'configMapPropertySourceLocator' defined in class path resource [org/springframework/cloud/kubernetes/fabric8/config/Fabric8BootstrapConfiguration.class]: Unsatisfied dependency expressed through method 'configMapPropertySourceLocator' parameter 0: No qualifying bean of type 'org.springframework.cloud.kubernetes.commons.config.ConfigMapConfigProperties' available: expected single matching bean but found 2: spring.cloud.kubernetes.config-org.springframework.cloud.kubernetes.commons.config.ConfigMapConfigProperties,configDataConfigMapConfigProperties
    ...
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.cloud.kubernetes.commons.config.ConfigMapConfigProperties' available: expected single matching bean but found 2: spring.cloud.kubernetes.config-org.springframework.cloud.kubernetes.commons.config.ConfigMapConfigProperties,configDataConfigMapConfigProperties
    ...   
```

# Test environment

## K8s : Minikube

### Start minikube
```
minikube start
```

### Switch to docker of minikube
```
eval $(minikube docker-env)
```

### Build the app and Docker container
```
./gradlew clean build && docker build . -t spring-k8s:latest
```

### Deploy in k8S
```
kubectl apply -f ./k8s
```