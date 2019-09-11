#!/usr/bin/env bash

# 设置项目根目录
base=$(cd `dirname $0`/..; pwd)

# 准备发布新版本
cd ${base}
git flow release start "v$1"

# 更新 pom.xml 版本号
mvn versions:set -DnewVersion=$1
mvn versions:commit

# 提交 pom.xml 版本号
git add .
git commit -m "chore: set version to v$1"

# 发布新版本
git flow release finish "v$1"
