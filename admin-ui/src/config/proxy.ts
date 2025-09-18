const proxy = {
	'/dev/': {
		target: 'http://192.168.31.154:8001',
		// target: 'https://h5-mall.xiaohogo.com/api/',
		changeOrigin: true,
		rewrite: (path: string) => path.replace(/^\/dev/, '')
	},

	'/prod/': {
		target: 'https://h5-mall.xiaohogo.com/api',
		changeOrigin: true,
		rewrite: (path: string) => path.replace(/^\/prod/, '/api')
	}
};

const value = 'dev';
const host = proxy[`/${value}/`]?.target;

export { proxy, host, value };
