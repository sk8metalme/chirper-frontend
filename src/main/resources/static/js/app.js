// Chirper - Custom JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 文字数カウント機能
    initCharacterCounter();

    // 自動消去するアラート
    initAutoHideAlerts();

    // ツイートフォームの送信確認
    initTweetFormValidation();

    // フォローボタンの確認
    initFollowButtonConfirmation();
});

/**
 * 文字数カウント機能の初期化
 */
function initCharacterCounter() {
    const tweetTextarea = document.querySelector('#content, #tweetContent');
    if (!tweetTextarea) return;

    const maxLength = 140;
    const counterElement = document.createElement('div');
    counterElement.className = 'char-count mt-2 text-end';
    counterElement.textContent = `0 / ${maxLength}`;

    tweetTextarea.parentElement.appendChild(counterElement);

    tweetTextarea.addEventListener('input', function() {
        const currentLength = this.value.length;
        counterElement.textContent = `${currentLength} / ${maxLength}`;

        // 文字数に応じてスタイルを変更
        counterElement.classList.remove('warning', 'danger');
        if (currentLength > maxLength) {
            counterElement.classList.add('danger');
        } else if (currentLength > maxLength * 0.9) {
            counterElement.classList.add('warning');
        }
    });
}

/**
 * アラートの自動非表示
 */
function initAutoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        // 5秒後に自動的にフェードアウト
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

/**
 * ツイートフォームのバリデーション
 */
function initTweetFormValidation() {
    const tweetForms = document.querySelectorAll('form[action*="/tweet"]');
    tweetForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const textarea = this.querySelector('textarea');
            if (!textarea) return;

            const content = textarea.value.trim();
            const maxLength = 140;

            if (content.length === 0) {
                e.preventDefault();
                alert('ツイート内容を入力してください');
                return false;
            }

            if (content.length > maxLength) {
                e.preventDefault();
                alert(`ツイートは${maxLength}文字以内で入力してください`);
                return false;
            }
        });
    });
}

/**
 * フォローボタンの確認
 */
function initFollowButtonConfirmation() {
    const unfollowButtons = document.querySelectorAll('form[action*="/unfollow"] button[type="submit"]');
    unfollowButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('フォローを解除しますか?')) {
                e.preventDefault();
                return false;
            }
        });
    });
}

/**
 * ハッシュタグとメンションのハイライト（将来実装用）
 */
function highlightHashtagsAndMentions(text) {
    // ハッシュタグをハイライト
    text = text.replace(/#(\w+)/g, '<a href="/search?q=%23$1" class="hashtag">#$1</a>');

    // メンションをハイライト
    text = text.replace(/@(\w+)/g, '<a href="/profile/$1" class="mention">@$1</a>');

    return text;
}

/**
 * 画像プレビュー機能（将来実装用）
 */
function initImagePreview() {
    const imageInputs = document.querySelectorAll('input[type="file"][accept="image/*"]');
    imageInputs.forEach(input => {
        input.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (!file) return;

            const reader = new FileReader();
            reader.onload = function(event) {
                const preview = document.createElement('img');
                preview.src = event.target.result;
                preview.className = 'img-thumbnail mt-2';
                preview.style.maxWidth = '200px';

                // 既存のプレビューを削除
                const existingPreview = input.parentElement.querySelector('.img-thumbnail');
                if (existingPreview) {
                    existingPreview.remove();
                }

                input.parentElement.appendChild(preview);
            };
            reader.readAsDataURL(file);
        });
    });
}

/**
 * タイムスタンプの相対表示（将来実装用）
 */
function formatRelativeTime(timestamp) {
    const now = new Date();
    const then = new Date(timestamp);
    const diff = Math.floor((now - then) / 1000); // 秒単位の差分

    if (diff < 60) return `${diff}秒前`;
    if (diff < 3600) return `${Math.floor(diff / 60)}分前`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}時間前`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}日前`;

    return then.toLocaleDateString('ja-JP');
}
