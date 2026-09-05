# 三数七余（Android）

一个极简、无网络权限、无第三方运行时依赖的原生 Android 小工具。

## 当前规则

- 启动即随机生成 3 个整数。
- 点击屏幕任意位置重新随机。
- 默认范围：1–999（包含 1 和 999，均匀取值）。
- 数字 `<= 7`：商、余数显示 `-`。
- 数字 `> 7`：商为整数除法 `n / 7`，余数为 `n % 7`。
- 示例：`10  3  8` → 商 `1  -  1` → 余数 `3  -  1`。
- `14` → 商 `2`，余数 `0`（标准数学余数）。

## 随机性

生产代码使用 `java.security.SecureRandom`，由 Android/系统安全随机源播种。它不是可以证明的“物理真随机”，但不是时间戳伪随机，也不是 `Math.random()`，属于密码学安全随机数生成方式。

## 修改随机范围

编辑：

`app/src/main/java/com/fonuhuo/sevenrandom/RandomEngine.java`

只改：

```java
public static final int MIN_VALUE = 1;
public static final int MAX_VALUE = 999;
```

## 构建

### GitHub Actions（推荐）

仓库已经包含 `.github/workflows/build-apk.yml`。推送到 `main` 后会自动：

1. 使用 JDK 17；
2. 安装 Android API 35 / Build Tools 35.0.0；
3. 使用 Gradle 8.10.2；
4. 先运行纯 Java 规则测试；
5. 构建已签名的 Debug APK；
6. 上传名为 `ThreeSevenRandom-apk` 的构建产物。

在 GitHub 仓库打开 **Actions → Build Android APK → 最新一次运行 → Artifacts** 即可下载。

也可以直接用 Android Studio 打开项目根目录后 Build APK。
项目故意不使用 Compose、AndroidX、网络库、数据库或图片资源，以减少启动开销和 APK 体积。
