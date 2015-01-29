# Infinispan Embedded Tutorial: Weather App

## Overview

This is a tutorial which explains how to use Infinispan embedded in your own
application. The application will retrieve the current weather conditions for
some cities and store them in a cache, for quicker retrieval. It will also
show how to configure the cache for expiration, so that entries get removed as
they age. It will then demonstrate how to cluster multiple nodes together and
reacting to events in the cluster. Finally, a computation of average temperatures
per country will show the power of the map/reduce functionality.

Each tagged commit is a separate lesson teaching a single aspect of Infinispan.

The tutorial instructions are at http://infinispan.org/tutorials/embedded/

N.B. The tutorial connects to OpenWeatherMap (http://openweathermap.org) to
retrieve current weather data. The service requires obtaining a free API key.
Before launching the application, ensure you've set the OWMAPIKEY environment
variable to your API key.
If you don't want to register for the service, if you don't have an Internet
connection or you are having trouble connecting to the service, just don't set
the environment variable, and it will use the RandomWeatherService.

## Prerequisites

### Git

- A good place to learn about setting up git is [here][git-github]
- Git [home][git-home] (download, documentation)

### JDK

- Get a [JDK][jdk-download]. You will need version 1.8 or higher

### Maven

- Get [Maven][maven-download]. You will need version 3.2 or higher

## Commits / Tutorial Outline

You can check out any point of the tutorial using
    git checkout step-?

To see the changes between any two lessons use the git diff command.
    git diff step-?..step-?

### step-0/setup

- The initial implementation of the Weather application

### step-1/cache-manager

- Adding Infinispan to the project and initializing a cache manager

### step-2/cache-put-get

- Add caching to the weather service

### step-3/expiration

- Store the entries in the cache with an expiration time

### step-4/cachemanager-configuration

- Configure the features of the default cache

### step-5/clustering

- Add a transport to the CacheManager so that it supports clustering

### step-6/cachemanager-listener

- Use CacheManager events to detect changes in the cluster

### step-7/cache-listener

- Detect changes to the cache with a clustered listener

### step-8/grouping

- Group related entries on the same nodes for efficiency

### step-9/externalizer

- Implement a custom externalizer for our value class

### step-10/mapreduce

- Use Map/Reduce to compute temperature averages per country

### step-11/declarative-configuration

- Use the XML configuration instead of the programmatic API

## Building and running the tutorial

- Run `mvn clean package` to rebuild the application
- Run `mvn exec:exec` to execute the application. In case you're running a clustered step, run this from
  multiple terminals, where each instance will represent a node

## Application Directory Layout

    src/                -->
      main/             -->
        java/           -->
        resources/      -->

## Contact

For more information on Infinispan please check out http://infinispan.org/

[jdk-download]: http://www.oracle.com/technetwork/articles/javase/index-jsp-138363.html
[git-home]: http://git-scm.com
[git-github]: http://help.github.com/set-up-git-redirect
[maven-download]: http://maven.apache.org/download.html

