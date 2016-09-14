#! /bin/bash

echo "publishing docs for: $PROJECT_VERSION"
git config --global user.email "evangelists@stormpath.com"
git config --global user.name "stormpath-sdk-java Auto Doc Build"
git clone git@github.com:stormpath/stormpath.github.io.git
cd stormpath.github.io
git fetch origin source:source
git checkout source

echo "Copying over servlet plugin docs"
rm -rf source/java/shiro-servlet-plugin/
cp -r ../extensions/servlet/docs/build/html source/java/shiro-servlet-plugin
cp -r ../extensions/servlet/docs/build/html source/java/shiro-servlet-plugin/latest
cp -r ../extensions/servlet/docs/build/html source/java/shiro-servlet-plugin/$PROJECT_VERSION

echo "Copying over javadocs"
rm -rf source/java/shiro/apidocs
cp -r ../target/site/apidocs source/java/shiro-apidocs
cp -r ../target/site/apidocs source/java/shiro-apidocs/latest
cp -r ../target/site/apidocs source/java/shiro-apidocs/$PROJECT_VERSION

git add --all
git commit -m "stormpath-sdk-java release $PROJECT_VERSION"
ls -la source/java/servlet-plugin
#git push origin source
gem install bundler
bundle install
rake setup_github_pages[git@github.com:stormpath/stormpath.github.io.git]
cd _deploy
git pull --no-edit -s recursive -X theirs https://github.com/stormpath/stormpath.github.io.git
cd ..
rake generate > /tmp/docs_generate.log
cd _deploy
git pull --no-edit -s recursive -X theirs https://github.com/stormpath/stormpath.github.io.git
cd ..
#rake deploy
cd ..
