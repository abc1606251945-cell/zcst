import { createHtmlPlugin } from 'vite-plugin-html'
import viteCompression from 'vite-plugin-compression'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'path'

export default function createVitePlugins(env, isBuild) {
  const vitePlugins = [
    // 使用svg图标
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
      symbolId: 'icon-[dir]-[name]',
      inject: 'body-last',  // 修复语法错误
      customDomId: '__svg__icons__dom__',
    }),
    // html相关
    createHtmlPlugin({
      minify: isBuild,
      inject: {
        data: {
          COPYRIGHT: env.VITE_APP_COPYRIGHT,
          TITLE: env.VITE_APP_TITLE,
        },
      },
    }),
  ]

  // 生产环境去除console
  if (isBuild) {
    vitePlugins.push(
      // compression
      viteCompression({
        algorithm: 'gzip',
        ext: '.gz',
      })
    )
  }

  return vitePlugins
}