# SOARS Samples ライブラリ

## 環境構築

以下では，Windows 環境を想定している．

### 必要ソフトウェア

- OpenJDK 11
  - <https://adoptopenjdk.net/>
    - OpenJDK 11 (LTS), HotSpot を選択
- VSCode
  - <https://code.visualstudio.com/>
    - Extension Pack for Java (Microsoft 社製）プラグインをインストール
- Maven
  - <https://maven.apache.org/>
- git for windows
  - <https://gitforwindows.org/>

### インストール

まず、VSCode をインストールして起動する。

次に、VSCode を起動して、Extension Pack for Java (Microsoft 社製)をインストールする。すると、Java のインストール画面になるので、OpenJDK 11, HotSpot を選択して、インストーラをダウンロードしたのち、インストールを行う。一旦、VSCode を終了する。

次に、Git for Windows のインストーラをダウンロードしてインストールする。インストーラでいろいろ聞かれるが、すべてデフォルト設定のまま進めてよい。

次に、以下を実行して git の初期設定を行う。`hoge`と`hoge@hoge.jp`は適宜読み替えてほしい。

```bash
git config --global user.name "hoge"
git config --global user.email "hoge@hoge.jp"
```

すると、ホームディレクトリに`.github`が出来上がるはずである。

`.github`

```bash
[user]
    name = hoge
    email = hoge@hoge.jp
```

次に、Maven の"Binary zip archive"の最新版を[ここ](https://maven.apache.org/download.cgi)からダウンロードして展開する。展開の結果、apache-maven-3.8.5 フォルダが得られたとすると、apache-maven-3.8.5 フォルダを、C:\Program Files\の下にコピーして、C:\Program Files\apache-maven-3.8.5\bin を、環境変数 PATH に登録する。

次に、Maven の設定を行う。[ここ](https://github.com/settings/tokens)から Personal access token をつくる．
初期値では 1 ヶ月でトークンの期限が切れるので，トークンの期限を変更したほうがよい．
デプロイしたい場合は，write:packages スコープにチェックする．ホームディレクトリに`.m2`フォルダを作成し、その下に下記の`settings.xml`を作成して，デプロイ先となる Maven リポジトリの認証情報を記述する．Github-Account-Name を自分の GitHub アカウント名，Github-Account-Token を上記で入手した Personal access token に置き換えること．

`settings.xml`

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

最後に、VSCode を起動して、エクスプローラ上の"Clone Repository"ボタンを押す。するとリポジトリ名を聞いてくるので、`https://github.com/soars-jp/samples`を入力してリターンキーを押すと、リポジトリを置くフォルダを聞いてくるので適当なフォルダを選択する。すると、リポジトリのダウンロードがはじまる。リポジトリのダウンロードが終わると、リポジトリを開くか聞いてくるので"Open"ボタンを押す。すると、エクスプローラ上にリポジトリが表示されるはずである。"View Projects"ボタンがポップアップで現れるが無視してかまわない。


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
- 2022/08/02 sample4の修正
- 2022/07/22 soars-core220722_01に対応．station.csvにTypeカラムを追加．pom.xmlの更新．
- 2022/03/23 sample4のバグを修正
- 2022/03/10 sample9の父親の人数を5人に増やした
- 2021/11/14 README.mdにsettings.xmlの設定情報を追加した．
- 2021/11/02 ライブラリの変更にともない，sample7を更新．
- 2021/10/31 sample7を作成した．
- 2021/10/29 sample1-6を作成した．
- 2021/10/13 soars-core-211013_01.jarを読み込むようにpom.xmlを修正した．
- 2021/09/14 パッケージ名sores-core-210914_01.jarに依存するようにpom.xmlを修正した．
