pluginManagement {
    repositories {

//        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/google") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/central") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/google") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/central") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicPlayerApplication"
include(":app")
 