#!/usr/bin/env bash

# 设置项目根目录
base=$(cd `dirname $0`/..; pwd)

# 执行数据库脚本
echo "首次运行将会建立开发数据库和产品数据库"
echo "再次运行只会重置开发数据库"
echo "确认请输入数据库密码，取消请直接按回车"
mysql -uroot -p < ${base}/manual/initial_db.sql 2>/dev/null

# 复制配置文件
if [[ -e "${base}/config/application.properties" ]]; then
    echo "检测到配置文件已存在，请修改该文件：${base}/config/application.properties"
else
    echo "正在复制密码配置文件，请修改该文件：${base}/config/application.properties"
    cp "${base}/config/application.properties.default" "${base}/config/application.properties"
fi
