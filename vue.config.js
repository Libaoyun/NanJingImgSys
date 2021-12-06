const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path')
const TerserPlugin = require('terser-webpack-plugin')
const CompressionPlugin = require("compression-webpack-plugin")

module.exports = {
    configureWebpack:{
        plugins: [
            new CopyWebpackPlugin([
                {
                    from: path.resolve(__dirname, './static'),
                    to: path.resolve(__dirname, './dist/static'),
                    ignore: ['.*']
                }
            ]),
            new CompressionPlugin({
                test:/\.js$|\.html$|\.css/, //匹配文件名
                threshold: 10240,//对超过10k的数据压缩
                deleteOriginalAssets: false //不删除源文件
            })
        ],
        optimization: {
            minimize: process.env.NODE_ENV === 'production',
            minimizer: [
                new TerserPlugin({
                    terserOptions: {
                        ecma: undefined,
                        warnings: false,
                        parse: {},
                        compress: {
                            drop_console: true,
                            drop_debugger: true,
                            pure_funcs: ['console.log'], // 移除console
                        },
                    },
                }),
            ]
        },
        performance: {
            hints:'warning',
            //入口起点的最大体积 整数类型（以字节为单位）
            maxEntrypointSize: 50000000,
            //生成文件的最大体积 整数类型（以字节为单位 300k）
            maxAssetSize: 30000000,
            //只给出 js 文件的性能提示
            assetFilter: function(assetFilename) {
                return assetFilename.endsWith('.js');
            }
        }
    },
    productionSourceMap: false,
    transpileDependencies: ['ant-design-vue'],
    devServer: {
        proxy: {
            '/rdexpense': {
                // target: 'http://192.168.41.180:1001',
                // target: 'http://192.168.41.202:1002',
                target: 'http://192.168.40.236:1001',//鲁
                ws: true,
                changeOrigin: true,
            }
        }
    }
}