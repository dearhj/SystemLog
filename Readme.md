### 目录结构  
1、app目录为demo代码。  
2、systemloglib目录为sdk源码。  
3、lib目录为根据sdk源码编译后的二进制文件。  
4、systemloghelp为一个系统后台服务，做数据中转用。  

### 注意事项 
1、demo app 可以依赖源码或者二进制文件，选择其中之一即可。  
```kotlin
implementation project(path: ':systemloglib')  //依赖源码
implementation 'com.android.systemloglib:systemloglib:1.0-20231103'   //依赖二进制文件
```
2、编译二进制文件指令  
```kotlin
gradlew publish  
```
3、由于demo app是普通应用，systemloglib中的部分接口没有权限使用，所以添加systemloghelp后台服务，systemloghelp是一个没有界面的系统权限apk,需要安装到设备中，作为数据中转作用。当前需要手动安装后调试验证，后期最终版本会将此服务预装进系统。  



