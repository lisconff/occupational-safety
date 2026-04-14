// 全局保存用户信息
let userData = {};

// 密码显示/隐藏
const togglePassword = document.getElementById('togglePassword');
const passwordInput = document.getElementById('password');
const repasswordInput = document.getElementById('repassword');

togglePassword.addEventListener('click', function () {
    // 切换密码可见性
    const type = passwordInput.type === 'password' ? 'text' : 'password';
    passwordInput.type = type;
    repasswordInput.type = type;

    this.textContent = type === 'password' ? '🙈' : '👁️';
});

// 获取来源页面（注册成功后跳转到登录页，并记住要回到哪个页面）
function getRedirectUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const redirect = urlParams.get('redirect');
    if (redirect) {
        return decodeURIComponent(redirect);
    }
    return null;
}

// 第一步：下一步按钮 
document.getElementById('nextBtn').onclick = function () {
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const repassword = document.getElementById('repassword').value.trim();

    const usernameRegex = /^[a-zA-Z0-9_\u4e00-\u9fa5]{2,20}$/;
    if (!usernameRegex.test(username)) {
        alert('用户名长度需在2-20位之间，仅支持中英文、数字、下划线');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('请输入正确的邮箱格式');
        return;
    }

    // 修正密码正则表达式（原代码有误）
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(password)) {
        alert('密码长度不能少于8位，且必须包含字母和数字');
        return;
    }

    if (password !== repassword) {
        alert('两次输入的密码不一致');
        return;
    }

    // 保存信息
    userData = { username, email, password };

    // 切换到第二步
    document.getElementById('step1').classList.remove('active');
    document.getElementById('step2').classList.add('active');
};

// 获取验证码（模拟）
document.getElementById('getCodeBtn').onclick = function () {
    const phone = document.getElementById('phone').value.trim();
    if (phone.length !== 11 || isNaN(phone)) {
        document.getElementById('codeTip').innerText = '请输入11位数字手机号';
        return;
    }
    document.getElementById('codeTip').innerText = '测试验证码：123456，请输入';
};

// 第二步：最终注册
const registerForm = document.getElementById('registerForm');
const registerBtn = document.getElementById('registerBtn');

registerForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const phone = document.getElementById('phone').value.trim();
    const code = document.getElementById('code').value.trim();

    // 校验验证码
    if (code !== '123456') {
        alert('验证码错误！测试验证码：123456');
        return;
    }

    userData.phone = phone;

    // loading 效果
    registerBtn.classList.add('loading');
    registerBtn.textContent = '注册中...';
    registerBtn.disabled = true;

    // 模拟请求
    setTimeout(() => {
        // 保存用户信息到 localStorage（用于登录验证）
        localStorage.setItem("user", JSON.stringify(userData));

        alert('注册成功！即将跳转到登录页');

        // 构建跳转URL，带上来源页面参数
        const redirectUrl = getRedirectUrl();
        let loginUrl = '../login/index.html';
        if (redirectUrl) {
            loginUrl += `?redirect=${encodeURIComponent(redirectUrl)}`;
        }

        window.location.href = loginUrl;
    }, 1500);
});