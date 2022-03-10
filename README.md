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

## サンプルプログラム
- [サンプル１：最も簡単なプログラム](./src/main/java/jp/soars/examples/sample01)
- [サンプル2：サンプル1の拡張（日を跨ぐ相対時刻指定）](./src/main/java/jp/soars/examples/sample02)
- [サンプル3：サンプル1の拡張（独自ルールの定義）](./src/main/java/jp/soars/examples/sample03)
- [サンプル4：サンプル3の拡張（役割の統合と役割の変更）](./src/main/java/jp/soars/examples/sample04)
- [サンプル5：繰り返し動作（ルールの中でルールの発火条件を指定）](./src/main/java/jp/soars/examples/sample05)
- [サンプル6：サンプル5の拡張（次の発火までの相対時刻を毎回ランダムに決定）](./src/main/java/jp/soars/examples/sample06)
- [サンプル7：スポット・エージェントの動的追加・削除](./src/main/java/jp/soars/examples/sample07)
- [サンプル8：列車への乗車（絶対時刻指定）](./src/main/java/jp/soars/examples/sample08)
- [サンプル9：サンプル8の修正（列車への乗車の相対時刻指定）](./src/main/java/jp/soars/examples/sample09)
## 履歴
- 2022/03/10 sample9の父親の人数を5人に増やした
- 2021/11/14 README.mdにsettings.xmlの設定情報を追加した．
- 2021/11/02 ライブラリの変更にともない，sample7を更新．
- 2021/10/31 sample7を作成した．
- 2021/10/29 sample1-6を作成した．
- 2021/10/13 soars-core-211013_01.jarを読み込むようにpom.xmlを修正した．
- 2021/09/14 パッケージ名sores-core-210914_01.jarに依存するようにpom.xmlを修正した．
