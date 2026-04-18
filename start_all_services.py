import subprocess
import time
import os
import sys

SERVICES = [
    "user-service",
    "item-service",
    "trade-service",
    "payment-service",
    "search-service",
    "message-service",
    "gateway-service"
]

def print_banner():
    print("=" * 50)
    print("  游戏道具交易平台 - 微服务启动脚本")
    print("=" * 50)
    print()

def get_project_root():
    return os.path.dirname(os.path.abspath(__file__))


def start_microservices():
    print("[2/2] 启动微服务...")
    print("-" * 40)
    root = get_project_root()

    for service in SERVICES:
        service_path = os.path.join(root, service)
        print(f"启动 {service} ...")
        subprocess.Popen(
            ["cmd", "/k", f"cd /d {service_path} && mvn spring-boot:run"],
            cwd=service_path,
            creationflags=subprocess.CREATE_NEW_CONSOLE
        )
        time.sleep(3)

def main():
    print_banner()
    start_microservices()

    print()
    print("=" * 50)
    print("  所有微服务已启动")
    print("=" * 50)
    print()
    print("微服务列表:")
    for service in SERVICES:
        print(f"  - {service}")
    print()
    print("基础设施:")
    print("  - MySQL        : localhost:3306")
    print("  - Redis        : localhost:6379")
    print("  - Nacos        : localhost:8848")
    print("  - RocketMQ     : localhost:9876")
    print("  - Elasticsearch: localhost:9200")
    print()
    print("前端开发服务器:")
    print("  - cd frontend && npm run dev")
    print()
    input("按 Enter 键退出...")

if __name__ == "__main__":
    main()
