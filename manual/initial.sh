#!/usr/bin/env bash

# 设置项目根目录
base=$(cd `dirname $0`/..; pwd)

# 复制配置文件
if [[ -e "${base}/config/application.properties" ]]; then
    echo "检测到配置文件已存在，请修改该文件：${base}/config/application.properties"
else
    echo "正在复制密码配置文件，请修改该文件：${base}/config/application.properties"
    cp "${base}/config/application.properties.default" "${base}/config/application.properties"
fi