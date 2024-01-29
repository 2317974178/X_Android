#### X_Android使用
<font color = "red">要使用本仓库需要在根目录添加：jitpack.io仓库
</font>

1.在根目录的setting.gradle中添加镜像：

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //阿里云镜像地址：
//central仓和jcenter仓的聚合仓
        maven { url 'https://jitpack.io' }
        maven { url 'https://maven.aliyun.com/repository/public' }
//google镜像地址
        maven { url 'https://maven.aliyun.com/repository/google' }
        google()
        mavenCentral()
    }
}
```

2.在根目录的build.gradle添加aspectjx依赖：

```groovy
buildscript {
    dependencies {
        classpath 'io.github.wurensen:gradle-android-plugin-aspectjx:3.3.2'
    }
}
```

3.在应用模块的build.gradle中添加如下配置：

```groovy
plugins {
    id 'com.android.application'
}
apply plugin: 'io.github.wurensen.android-aspectjx'

android {
    aspectjx {
        exclude 'org.jetbrains.kotlin', "module-info", 'versions.9'
    }
}

dependencies {
    implementation 'com.github.2317974178:X_Android:1.0.0'
}
```

4.动态权限框架使用

```java
 @PermissionRequest(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},requestCode = 1)
    public void getPermission(){
		//权限申请
    }

    @PermissionGranted
    public void permissionGranted() {
        Log.d(TAG, "permissionGranted: 权限申请成功");
    }

    @PermissionReject
    public void permissionReject(){
        Log.d(TAG, "permissionReject: 拒绝权限");
    }
```

5.mqttL-library使用方法

> 在gradle.properties中添加：
> 
> android.enableJetifier=true 

