import subprocess
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
    print("  游戏道具交易平台 - 停止所有服务")
    print("=" * 50)
    print()

def get_project_root():
    return os.path.dirname(os.path.abspath(__file__))

def stop_docker_infra():
    print("[1/2] 停止基础设施服务 (Docker)...")
    print("-" * 40)
    root = get_project_root()
    subprocess.run(
        ["docker", "compose", "-f", "deploy/docker-compose-infra.yml", "down"],
        cwd=root
    )
    print()

def stop_microservices():
    print("[2/2] 停止微服务窗口...")
    print("-" * 40)

    for service in SERVICES:
        print(f"停止 {service} ...")
        subprocess.run(
            ["taskkill", "/FI", f"WINDOWTITLE eq {service}*", "/F"],
            capture_output=True
        )

    print()
    print("停止前端开发服务器...")
    subprocess.run(["taskkill", "/FI", "WINDOWTITLE eq npm*", "/F"], capture_output=True)
    subprocess.run(["taskkill", "/FI", "WINDOWTITLE eq vite*", "/F"], capture_output=True)

def main():
    print_banner()
    stop_docker_infra()
    stop_microservices()

    print()
    print("=" * 50)
    print("  所有服务已停止")
    print("=" * 50)
    print()
    input("按 Enter 键退出...")

if __name__ == "__main__":
    main()
