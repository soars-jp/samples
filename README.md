# 環境設定

Mavenがインストールされていなければ，まず，Mavenをインストールする．UbuntuなどのDebian系の場合は，aptでインストールできる．

`Mavenのインストール`

```
sudo apt install maven
```

Personal access tokenを作る．https://github.com/settings/tokens からPersonal access tokenをつくる．
デプロイしたい場合は，write:packagesスコープにチェックする．

下記の~/.m2/settings.xml を作成して，デプロイ先となるMavenリポジトリの認証情報を記述する．
Github-Account-Nameを自分のGitHubアカウント名，Github-Account-Tokenを上記で入手したPersonal access tokenに置き換えること．

`~/.m2/settings.xml`

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <server>
      <id>github</id>
      <username>Github-Account-Name</username>
      <password>Github-Account-Token</password>
    </server>
  </servers>

</settings>

```