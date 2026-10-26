import * as echarts from 'echarts'

/**
 * 初始化并管理一个 ECharts 实例。
 *
 * @param {HTMLElement|string} container 图表容器元素或容器选择器
 * @param {Object} option ECharts 配置项
 * @param {string|Object} [theme] ECharts 主题
 * @param {Object} [initOpts] ECharts 初始化参数
 * @returns {{ chart: import('echarts').ECharts, setOption: Function, resize: Function, dispose: Function }}
 */
export function createChart(container, option = {}, theme, initOpts) {
  const element = resolveContainer(container)

  if (!element) {
    throw new Error('ECharts 容器不存在')
  }

  let chart = echarts.getInstanceByDom(element)
  if (!chart) {
    chart = echarts.init(element, theme, initOpts)
  }

  chart.setOption(option)

  const resize = () => {
    if (!chart.isDisposed()) chart.resize()
  }

  const resizeObserver = typeof ResizeObserver !== 'undefined'
    ? new ResizeObserver(resize)
    : null

  resizeObserver?.observe(element)

  const dispose = () => {
    resizeObserver?.disconnect()
    if (!chart.isDisposed()) chart.dispose()
  }

  return {
    chart,
    setOption(nextOption, notMerge = false, lazyUpdate = false) {
      if (!chart.isDisposed()) {
        chart.setOption(nextOption, notMerge, lazyUpdate)
      }
    },
    resize,
    dispose
  }
}

/**
 * 获取容器对应的 ECharts 实例。
 *
 * @param {HTMLElement|string} container 图表容器元素或容器选择器
 * @returns {import('echarts').ECharts|null}
 */
export function getChart(container) {
  const element = resolveContainer(container)
  return element ? echarts.getInstanceByDom(element) || null : null
}

/**
 * 调整图表尺寸。
 *
 * @param {HTMLElement|string} container 图表容器元素或容器选择器
 */
export function resizeChart(container) {
  getChart(container)?.resize()
}

/**
 * 销毁图表实例并释放资源。
 *
 * @param {HTMLElement|string} container 图表容器元素或容器选择器
 */
export function disposeChart(container) {
  getChart(container)?.dispose()
}

function resolveContainer(container) {
  if (typeof container === 'string') {
    return document.querySelector(container)
  }

  return container instanceof HTMLElement ? container : null
}

export { echarts }

export default {
  createChart,
  getChart,
  resizeChart,
  disposeChart,
  echarts
}