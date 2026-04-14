// 获取页面元素
const loginForm = document.getElementById('loginForm');
const loginBtn = document.getElementById('loginBtn');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const togglePassword = document.getElementById('togglePassword');

// 小眼睛：点击切换显示/隐藏密码
togglePassword.addEventListener('click', function () {
    const type = passwordInput.type === 'password' ? 'text' : 'password';
    passwordInput.type = type;
    this.textContent = type === 'password' ? '👁️' : '🙈';
});

// 获取来源页面（登录成功后跳转回这个页面）
function getRedirectUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const redirect = urlParams.get('redirect');
    if (redirect) {
        return decodeURIComponent(redirect);
    }
    // 默认跳转到论坛首页
    return '../index.html';
}

// 固定账户配置
const FIXED_ACCOUNT = {
    email: 'Zhituan@a.com',
    password: 'zta123456',
    username: '职途安',
    avatar: '职途'
};

// 表单提交（登录按钮点击）
loginForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('请输入正确的邮箱格式');
        return;
    }

    if (password.length < 8) {
        alert('密码长度不能少于8位');
        return;
    }

    loginBtn.classList.add('loading');
    loginBtn.textContent = 'Logging in...';
    loginBtn.disabled = true;

    setTimeout(() => {
        // ========== 优先检查固定账户 ==========
        const isFixedAccount = email === FIXED_ACCOUNT.email && password === FIXED_ACCOUNT.password;
        
        // 检查注册用户
        const registeredUser = JSON.parse(localStorage.getItem('user'));
        const isRegisteredUser = registeredUser 
            && registeredUser.email === email 
            && registeredUser.password === password;

        const isLoginSuccess = isFixedAccount || isRegisteredUser;
        
        // 获取用户名（固定账户使用预设用户名，注册用户使用注册时的用户名）
        let realUsername = '';
        if (isFixedAccount) {
            realUsername = FIXED_ACCOUNT.username;
        } else if (isRegisteredUser) {
            realUsername = registeredUser.username;
        }

        if (isLoginSuccess) {
            const rememberMe = document.getElementById('rememberMe').checked;
            const userToken = 'mock_token_' + Date.now();
            const userAvatar = realUsername.substring(0, 2).toUpperCase();

            // 统一登录状态存储
            const loginData = {
                isLoggedIn: true,
                token: userToken,
                username: realUsername,
                avatar: userAvatar,
                email: email,
                loginTime: new Date().toLocaleString()
            };

            if (rememberMe) {
                localStorage.setItem('userToken', userToken);
                localStorage.setItem('userName', realUsername);
                localStorage.setItem('isLoggedIn', 'true');
                localStorage.setItem('currentUser', JSON.stringify(loginData));
            } else {
                sessionStorage.setItem('userToken', userToken);
                sessionStorage.setItem('userName', realUsername);
                sessionStorage.setItem('isLoggedIn', 'true');
                sessionStorage.setItem('currentUser', JSON.stringify(loginData));
            }

            alert('登录成功！');

            const redirectUrl = getRedirectUrl();
            window.location.href = redirectUrl;
        } else {
            alert('登录失败，请检查账号密码');
            loginBtn.classList.remove('loading');
            loginBtn.textContent = 'Log in';
            loginBtn.disabled = false;
        }
    }, 1500);
});