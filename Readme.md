### 本工程中app目录为demo代码， systemloglib目录为sdk源码， lib目录为根据sdk源码编译后的二进制文件。  
### demo app 可以依赖源码或者二进制文件，选择其中之一即可。  
### 依赖源码： implementation project(path: ':systemloglib')   
### 依赖二进制文件： implementation 'com.android.systemloglib:systemloglib:1.0-20231103'   且需要配置二进制文件路径 maven { url 'E:\\githubCode\\SystemLog\\lib' }     //二进制文件保存路径   
### 编译二进制文件指令：  gradlew publish

### 具体接口调用方法，请参考demo代码。  编译demo时注意修改jdk路径。
### app/build/output目录下已有一个编译好的demo app.



