# SOARS Samples ライブラリ

## 環境設定

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

## 履歴
- 2021/10/13 soars-core-211013_01.jarを読み込むようにpom.xmlを修正した．
- 2021/09/14 パッケージ名sores-core-210914_01.jarに依存するようにpom.xmlを修正した．
- 2021/09/14 [soars-d2j-work](https://github.com/soars-jp/soars-d2j-work)の2021/09/12版から分岐させて，リポジトリを作成した．
