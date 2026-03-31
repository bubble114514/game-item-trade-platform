<template>
  <div>
    <div class="grid">
      <div class="card">
        <h2>1. 道具搜索（ES）</h2>

        <label for="q">关键词</label>
        <input id="q" v-model="q" placeholder="输入道具名/分类/游戏，例如：武器" />
        <button class="primary" @click="searchItems">搜索</button>

        <div class="msg" :class="{ ok: searchOk }">{{ searchMsg }}</div>

        <div style="max-height: 300px; overflow:auto; margin-top: 10px;">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>游戏</th>
                <th>分类</th>
                <th>参考价</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="items.length === 0">
                <td colspan="5" class="danger">未找到结果</td>
              </tr>
              <tr
                v-for="it in items"
                :key="it.id"
                style="cursor:pointer"
                @click="selectItem(it)"
              >
                <td>{{ it.id }}</td>
                <td>{{ it.name }}</td>
                <td>{{ it.game }}</td>
                <td>{{ it.category }}</td>
                <td>{{ it.referencePrice ?? '' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="muted" style="margin-top: 10px;">
          提示：搜索依赖 Elasticsearch 索引数据（后续可写“同步任务/定时任务”）。
        </div>
      </div>

      <div class="card">
        <h2>2. 道具详情（MySQL + Redis）</h2>

        <label for="itemId">道具 ID</label>
        <input id="itemId" v-model="itemId" placeholder="例如：1" />
        <button class="primary" @click="getItem">查询道具</button>

        <div class="msg" :class="{ ok: itemOk }">{{ itemMsg }}</div>
        <pre v-if="itemJson" class="msg ok" style="background:#f8fafc;border-radius:8px;padding:12px;white-space:pre-wrap;">{{ itemJson }}</pre>
      </div>
    </div>

    <div class="grid" style="margin-top: 14px;">
      <div class="card">
        <h2>3. 创建订单（REST）</h2>

        <div class="two">
          <div>
            <label for="buyerId">买家 ID（自动）</label>
            <input id="buyerId" v-model="buyerId" disabled />
          </div>
          <div>
            <label for="sellerId">卖家 ID</label>
            <input id="sellerId" v-model="sellerId" placeholder="例如：2" />
          </div>
        </div>

        <label for="orderItemId">道具 ID</label>
        <input id="orderItemId" v-model="orderItemId" placeholder="例如：1" />

        <label for="amount">金额</label>
        <input id="amount" v-model="amount" placeholder="例如：99.90" />

        <button class="primary" @click="createOrder">提交订单</button>
        <div class="msg" :class="{ ok: orderOk }">{{ orderMsg }}</div>

        <div class="muted" style="margin-top: 8px;">可用返回的订单 ID 进行下一步“模拟支付”。</div>
      </div>

      <div class="card">
        <h2>4. 模拟支付（解耦）</h2>

        <label for="payOrderId">订单 ID</label>
        <input id="payOrderId" v-model="payOrderId" placeholder="例如：1001" />
        <button class="primary" @click="mockPay">模拟支付</button>
        <div class="msg" :class="{ ok: payOk }">{{ payMsg }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { request } from '../api/client.js'

const q = ref('')
const items = ref([])
const searchMsg = ref('')
const searchOk = ref(false)

const itemId = ref('')
const itemJson = ref('')
const itemMsg = ref('')
const itemOk = ref(false)

const buyerId = ref('')
const sellerId = ref('')
const orderItemId = ref('')
const amount = ref('')

const orderMsg = ref('')
const orderOk = ref(false)

const payOrderId = ref('')
const payMsg = ref('')
const payOk = ref(false)

function selectItem(it) {
  itemId.value = String(it.id)
  orderItemId.value = String(it.id)
}

async function searchItems() {
  searchMsg.value = ''
  searchOk.value = false
  items.value = []

  const keyword = q.value.trim()
  if (!keyword) {
    searchMsg.value = '请输入关键词'
    return
  }

  try {
    const json = await request('/search/items?q=' + encodeURIComponent(keyword) + '&size=20', {
      method: 'GET'
    })
    items.value = json.data || []
    searchOk.value = true
    searchMsg.value = '搜索完成'
  } catch (e) {
    searchMsg.value = e?.message || '搜索失败'
  }
}

async function getItem() {
  itemMsg.value = ''
  itemOk.value = false
  itemJson.value = ''

  const id = itemId.value.trim()
  if (!id) {
    itemMsg.value = '请输入道具 ID'
    return
  }

  try {
    const json = await request('/item/' + encodeURIComponent(id), { method: 'GET' })
    itemJson.value = JSON.stringify(json.data, null, 2)
    itemOk.value = true
    itemMsg.value = '查询完成'
  } catch (e) {
    itemMsg.value = e?.message || '查询失败'
  }
}

async function createOrder() {
  orderMsg.value = ''
  orderOk.value = false

  const buyer = buyerId.value
  const seller = sellerId.value.trim()
  const item = orderItemId.value.trim()
  const amt = amount.value.trim()

  if (!buyer) {
    orderMsg.value = '请先登录'
    return
  }
  if (!seller || !item || !amt) {
    orderMsg.value = '请填写卖家 ID / 道具 ID / 金额'
    return
  }

  try {
    const body = {
      buyerId: Number(buyer),
      sellerId: Number(seller),
      itemId: Number(item),
      amount: Number(amt)
    }
    const json = await request('/trade/orders', {
      method: 'POST',
      body: JSON.stringify(body)
    })
    orderOk.value = true
    orderMsg.value = '下单成功：'
    orderMsg.value += JSON.stringify(json.data, null, 2)
    payOrderId.value = String(json.data?.id ?? '')
  } catch (e) {
    orderMsg.value = e?.message || '下单失败'
  }
}

async function mockPay() {
  payMsg.value = ''
  payOk.value = false

  const oid = payOrderId.value.trim()
  if (!oid) {
    payMsg.value = '请输入订单 ID'
    return
  }

  try {
    const json = await request('/payment/orders/' + encodeURIComponent(oid) + '/mock-pay', {
      method: 'POST'
    })
    payOk.value = true
    payMsg.value = '支付结果：\n' + JSON.stringify(json.data, null, 2)
  } catch (e) {
    payMsg.value = e?.message || '支付失败'
  }
}

onMounted(() => {
  buyerId.value = localStorage.getItem('userId') || ''
})
</script>

