// ===== USER =====
function getUser() {
    const u = localStorage.getItem('se_user');
    return u ? JSON.parse(u) : null;
}

function saveUser(user) { localStorage.setItem('se_user', JSON.stringify(user)); }

function logout() {
    localStorage.removeItem('se_user');
    window.location.href = 'Login.html';
}

// ===== CART =====
function getCart() {
    const c = localStorage.getItem('se_cart');
    return c ? JSON.parse(c) : [];
}

function saveCart(cart) { localStorage.setItem('se_cart', JSON.stringify(cart)); }

function addToCart(id, name, price, category, imageUrl, qty) {
    id  = Number(id);
    qty = Number(qty) || 1;
    if (!id || isNaN(id)) { showToast('Invalid product'); return; }
    const cart = getCart();
    const existing = cart.find(i => Number(i.id) === id);
    if (existing) {
        existing.qty += qty;
    } else {
        cart.push({ id, name, price: Number(price), category: category || '', qty, imageUrl: imageUrl || '' });
    }
    saveCart(cart);
    updateCartCount();
    showToast(`✓ ${name} added to cart`);
    updateCartDrawer();
}

function removeFromCart(id) {
    saveCart(getCart().filter(i => Number(i.id) !== Number(id)));
    updateCartCount();
    updateCartDrawer();
}

function changeQty(id, delta) {
    let cart = getCart();
    const item = cart.find(i => Number(i.id) === Number(id));
    if (!item) return;
    item.qty += delta;
    if (item.qty <= 0) cart = cart.filter(i => Number(i.id) !== Number(id));
    saveCart(cart);
    updateCartCount();
    updateCartDrawer();
}

function clearCart() { saveCart([]); updateCartCount(); updateCartDrawer(); }

function updateCartCount() {
    const count = getCart().reduce((s, i) => s + i.qty, 0);
    document.querySelectorAll('.cart-count').forEach(el => el.textContent = count);
}

function cartTotal() {
    return getCart().reduce((s, i) => s + i.price * i.qty, 0);
}

// ===== WISHLIST =====
function getWishlist() {
    const w = localStorage.getItem('se_wishlist');
    return w ? JSON.parse(w) : [];
}

function saveWishlist(wl) { localStorage.setItem('se_wishlist', JSON.stringify(wl)); }

function toggleWishlist(id, name, price, category, imageUrl) {
    id = Number(id);
    const wl = getWishlist();
    const idx = wl.findIndex(i => Number(i.id) === id);
    if (idx > -1) {
        wl.splice(idx, 1);
        saveWishlist(wl);
        updateWishlistCount();
        showToast(`Removed from wishlist`);
    } else {
        wl.push({ id, name, price: Number(price), category: category || '', imageUrl: imageUrl || '' });
        saveWishlist(wl);
        updateWishlistCount();
        showToast(`♡ ${name} saved to wishlist`);
    }
    document.querySelectorAll(`.wishlist-btn[data-id="${id}"]`).forEach(btn => {
        btn.classList.toggle('active', isInWishlist(id));
    });
}

function isInWishlist(id) {
    return getWishlist().some(i => Number(i.id) === Number(id));
}

function updateWishlistCount() {
    const count = getWishlist().length;
    document.querySelectorAll('.wishlist-count').forEach(el => el.textContent = count);
}

// ===== CART DRAWER =====
function openCart() {
    document.getElementById('cartOverlay')?.classList.add('open');
    document.getElementById('cartDrawer')?.classList.add('open');
    updateCartDrawer();
}

function closeCart() {
    document.getElementById('cartOverlay')?.classList.remove('open');
    document.getElementById('cartDrawer')?.classList.remove('open');
}

function updateCartDrawer() {
    const body   = document.getElementById('cartDrawerBody');
    const footer = document.getElementById('cartDrawerFooter');
    if (!body) return;
    const cart = getCart();

    if (!cart.length) {
        body.innerHTML = `
            <div class="cart-empty">
                <div class="cart-empty-icon">🛍️</div>
                <h4>Your cart is empty</h4>
                <p>Add some items to get started</p>
            </div>`;
        if (footer) footer.style.display = 'none';
        return;
    }

    if (footer) footer.style.display = 'block';

    body.innerHTML = cart.map(item => {
        const imgContent = (item.imageUrl && item.imageUrl !== 'null' && item.imageUrl !== '')
            ? `<img src="${item.imageUrl}" alt="${item.name}"
                   style="width:100%;height:100%;object-fit:cover;"
                   onerror="this.parentElement.innerHTML='<div style=\\'width:100%;height:100%;display:flex;align-items:center;justify-content:center;font-size:10px;color:var(--light-grey);letter-spacing:1px;text-transform:uppercase;text-align:center;\\'>No<br>Image</div>'">`
            : `<div style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;font-size:10px;color:var(--light-grey);letter-spacing:1px;text-transform:uppercase;text-align:center;">No<br>Image</div>`;
        return `
        <div class="cart-item">
            <div class="cart-item-img" style="padding:0;overflow:hidden;">
                ${imgContent}
            </div>
            <div class="cart-item-info">
                <div class="cart-item-name">${item.name}</div>
                <div class="cart-item-cat">${item.category}</div>
                <div class="cart-item-price">Rs. ${(item.price * item.qty).toLocaleString()}</div>
                <div class="qty-controls">
                    <button class="qty-btn" onclick="changeQty(${item.id},-1)">−</button>
                    <div class="qty-display">${item.qty}</div>
                    <button class="qty-btn" onclick="changeQty(${item.id},1)">+</button>
                </div>
            </div>
            <button class="cart-item-remove" onclick="removeFromCart(${item.id})">✕</button>
        </div>`;
    }).join('');

    const total = cartTotal();
    document.getElementById('cartSubtotal').textContent = 'Rs. ' + total.toLocaleString();
    document.getElementById('cartTotalAmt').textContent = 'Rs. ' + total.toLocaleString();
}

// ===== NAV AUTH =====
function updateNav() {
    const user       = getUser();
    const loginLink  = document.getElementById('navLogin');
    const logoutLink = document.getElementById('navLogout');
    const adminLink  = document.getElementById('navAdmin');
    const accountLink= document.getElementById('navAccount');
    const wishLink   = document.getElementById('navWishlist');
    const profileLink= document.getElementById('navProfile');

    if (user) {
        if (loginLink)   loginLink.classList.add('hidden');
        if (logoutLink)  logoutLink.classList.remove('hidden');
        if (accountLink) accountLink.classList.remove('hidden');
        if (profileLink) profileLink.classList.remove('hidden');
        if (adminLink && user.role === 'ADMIN') adminLink.classList.remove('hidden');
    } else {
        if (loginLink)   loginLink.classList.remove('hidden');
        if (logoutLink)  logoutLink.classList.add('hidden');
        if (accountLink) accountLink.classList.add('hidden');
        if (adminLink)   adminLink.classList.add('hidden');
        if (profileLink) profileLink.classList.add('hidden');
    }

    if (wishLink) wishLink.classList.remove('hidden');
    updateWishlistCount();
    initMobileNav();
}

// ===== MOBILE HAMBURGER NAV =====
function initMobileNav() {
    if (document.getElementById('hamburgerBtn')) {
        _buildMobileMenu(); // refresh content (auth state may have changed)
        return;
    }

    const navLeft = document.querySelector('.navbar .nav-left');
    if (!navLeft) return;

    // Create hamburger button
    const btn = document.createElement('button');
    btn.id = 'hamburgerBtn';
    btn.className = 'hamburger-btn';
    btn.setAttribute('aria-label', 'Open menu');
    btn.innerHTML = '<span></span><span></span><span></span>';
    navLeft.insertBefore(btn, navLeft.firstChild);

    // Create mobile menu overlay
    const menu = document.createElement('div');
    menu.id = 'mobileMenu';
    menu.className = 'mobile-menu';
    // Position below navbar (60px on mobile, 72px on desktop — update dynamically)
    const updateMenuTop = () => {
        const nb = document.querySelector('.navbar');
        if (nb) menu.style.top = nb.offsetHeight + 'px';
    };
    window.addEventListener('resize', updateMenuTop);
    updateMenuTop();
    document.body.appendChild(menu);

    // Toggle open/close
    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const open = btn.classList.toggle('open');
        menu.classList.toggle('open', open);
        document.body.style.overflow = open ? 'hidden' : '';
    });

    // Close when a link is clicked
    menu.addEventListener('click', () => {
        btn.classList.remove('open');
        menu.classList.remove('open');
        document.body.style.overflow = '';
    });

    // If user resizes back to desktop, close menu and unlock body
    window.addEventListener('resize', () => {
        if (window.innerWidth > 900) {
            btn.classList.remove('open');
            menu.classList.remove('open');
            document.body.style.overflow = '';
        }
    });

    _buildMobileMenu();
}

function _buildMobileMenu() {
    const menu = document.getElementById('mobileMenu');
    if (!menu) return;
    const user = getUser();

    const items = [];

    // Shop links (always visible)
    items.push({ label: 'Shop', type: 'section' });
    items.push({ href: 'index.html',    text: 'Home' });
    items.push({ href: 'Products.html', text: 'Shop All' });
    items.push({ href: 'Wishlist.html', text: `♡ Wishlist (${getWishlist().length})` });

    // Account links
    items.push({ label: 'Account', type: 'section' });
    if (user) {
        items.push({ href: 'Orders.html',  text: 'My Orders' });
        items.push({ href: 'Profile.html', text: 'Profile' });
        if (user.role === 'ADMIN') {
            items.push({ href: 'Admin.html', text: '⚙ Admin Panel' });
        }
        items.push({ onclick: 'logout()', text: 'Logout', danger: true });
    } else {
        items.push({ href: 'Login.html',    text: 'Login' });
        items.push({ href: 'Register.html', text: 'Create Account' });
    }

    menu.innerHTML = items.map(item => {
        if (item.type === 'section') {
            return `<span class="mobile-menu-link section-title-mob">${item.label}</span>`;
        }
        if (item.onclick) {
            return `<button class="mobile-menu-link${item.danger ? ' danger' : ''}" onclick="${item.onclick}">${item.text}</button>`;
        }
        return `<a href="${item.href}" class="mobile-menu-link">${item.text}</a>`;
    }).join('');
}

// ===== STAR RATING HTML =====
function starsHTML(rating, interactive, onClickFn) {
    const r = Math.round(Number(rating) * 2) / 2;
    let html = '<span class="stars">';
    for (let i = 1; i <= 5; i++) {
        if (interactive) {
            html += `<span class="star" onclick="${onClickFn}(${i})" style="cursor:pointer;">`;
        } else {
            html += '<span class="star">';
        }
        if (r >= i)      html += '★';
        else if (r >= i - 0.5) html += '½';
        else             html += '☆';
        html += '</span>';
    }
    return html + '</span>';
}

// ===== PRODUCT CARD =====
function productCardHTML(p) {
    const pid = Number(p.id || p.productId);
    const hasDiscount = p.discountedPrice !== null
                     && p.discountedPrice !== undefined
                     && Number(p.discountedPrice) > 0
                     && Number(p.discountedPrice) < Number(p.price);
    const finalPrice = hasDiscount ? p.discountedPrice : p.price;
    const isLow  = p.stock > 0 && p.stock < 10;
    const isOut  = p.stock === 0;
    const imgUrl = (p.imageUrl && p.imageUrl !== 'null' && p.imageUrl !== '') ? p.imageUrl : '';
    const inWL   = isInWishlist(pid);

    const imageContent = imgUrl
        ? `<img src="${imgUrl}" alt="${p.name}"
               style="width:100%;height:100%;object-fit:cover;"
               onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
           <div style="display:none;width:100%;height:100%;align-items:center;justify-content:center;background:var(--cream);">
               <div style="text-align:center;color:var(--light-grey);">
                   <div style="font-size:10px;letter-spacing:2px;text-transform:uppercase;">No Image</div>
               </div>
           </div>`
        : `<div style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;background:var(--cream);">
               <div style="font-size:10px;letter-spacing:2px;text-transform:uppercase;color:var(--light-grey);">No Image</div>
           </div>`;

    const nameEsc  = p.name.replace(/'/g, "\\'");
    const imgUrlEsc = imgUrl.replace(/'/g, "\\'");

    return `
        <div class="product-card" onclick="goToProduct(${pid})">
            <div class="product-img" style="padding:0;overflow:hidden;position:relative;">
                ${imageContent}
                ${hasDiscount ? '<span class="product-badge sale" style="position:absolute;top:12px;left:12px;">Sale</span>' : ''}
                ${isLow && !hasDiscount ? '<span class="product-badge new" style="position:absolute;top:12px;left:12px;">Low Stock</span>' : ''}
                <button class="wishlist-btn ${inWL ? 'active' : ''}" data-id="${pid}"
                    onclick="event.stopPropagation();toggleWishlist(${pid},'${nameEsc}',${finalPrice},'${p.category}','${imgUrlEsc}')"
                    title="${inWL ? 'Remove from Wishlist' : 'Add to Wishlist'}">
                    ${inWL ? '♥' : '♡'}
                </button>
                <div class="product-actions" onclick="event.stopPropagation()">
                    <button class="btn-add-cart"
                        onclick="addToCart(${pid},'${nameEsc}',${finalPrice},'${p.category}','${imgUrlEsc}')"
                        ${isOut ? 'disabled style="opacity:0.5;cursor:not-allowed;"' : ''}>
                        ${isOut ? 'Out of Stock' : '+ Add to Cart'}
                    </button>
                    <button class="btn-quick-view" title="Quick View"
                        onclick="quickView(${pid})">👁</button>
                </div>
            </div>
            <div class="product-info">
                <div class="product-category">${p.category}</div>
                <div class="product-name">${p.name}</div>
                <div class="product-price">
                    <span class="price-current">Rs. ${Number(finalPrice).toLocaleString()}</span>
                    ${hasDiscount
                        ? `<span class="price-original">Rs. ${Number(p.price).toLocaleString()}</span>
                           <span class="price-discount">${Math.round((1 - Number(finalPrice) / Number(p.price)) * 100)}% OFF</span>`
                        : ''}
                </div>
                <div class="stock-indicator ${isOut ? 'stock-out' : isLow ? 'stock-low' : 'stock-ok'}">
                    ${isOut ? '✕ Out of Stock' : isLow ? `⚠ Only ${p.stock} left` : '✓ In Stock'}
                </div>
            </div>
        </div>
    `;
}

function goToProduct(id) {
    window.location.href = `ProductDetail.html?id=${id}`;
}

function quickView(id) {
    fetch(`/api/products/${id}`)
        .then(r => r.json())
        .then(p => {
            const pid = Number(p.id || p.productId);
            const hasDiscount = p.discountedPrice !== null
                             && p.discountedPrice !== undefined
                             && Number(p.discountedPrice) > 0
                             && Number(p.discountedPrice) < Number(p.price);
            const finalPrice = hasDiscount ? p.discountedPrice : p.price;
            const imgUrl = (p.imageUrl && p.imageUrl !== 'null' && p.imageUrl !== '') ? p.imageUrl : '';
            const nameEsc = p.name.replace(/'/g, "\\'");
            const imgUrlEsc = imgUrl.replace(/'/g, "\\'");

            const imageHTML = imgUrl
                ? `<img src="${imgUrl}" alt="${p.name}"
                       style="width:100%;height:100%;object-fit:cover;border-radius:4px;">`
                : `<div style="width:100%;height:100%;min-height:280px;display:flex;flex-direction:column;
                               align-items:center;justify-content:center;background:var(--cream);">
                       <div style="font-size:11px;letter-spacing:2px;text-transform:uppercase;
                                   color:var(--light-grey);">No Image Available</div>
                   </div>`;

            document.getElementById('quickViewContent').innerHTML = `
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:32px;align-items:start;">
                    <div style="background:var(--cream);min-height:320px;border:1px solid var(--border);
                                border-radius:4px;overflow:hidden;display:flex;align-items:center;justify-content:center;">
                        ${imageHTML}
                    </div>
                    <div>
                        <div style="font-size:10px;letter-spacing:2px;text-transform:uppercase;color:var(--grey);margin-bottom:8px;">
                            ${p.category}
                        </div>
                        <h2 style="font-family:'Playfair Display',serif;font-size:24px;font-weight:500;margin-bottom:16px;color:var(--black);">
                            ${p.name}
                        </h2>
                        <div style="margin-bottom:16px;display:flex;align-items:center;gap:12px;">
                            <span style="font-size:24px;font-weight:700;color:var(--black);">
                                Rs. ${Number(finalPrice).toLocaleString()}
                            </span>
                            ${hasDiscount
                                ? `<span style="font-size:14px;color:var(--light-grey);text-decoration:line-through;">
                                       Rs. ${Number(p.price).toLocaleString()}
                                   </span>
                                   <span style="font-size:12px;font-weight:700;color:var(--danger);background:#fdf5f5;padding:3px 8px;border-radius:2px;">
                                       ${Math.round((1 - Number(finalPrice) / Number(p.price)) * 100)}% OFF
                                   </span>`
                                : ''}
                        </div>
                        <p style="font-size:13px;color:var(--grey);line-height:1.8;margin-bottom:20px;
                                  padding-bottom:20px;border-bottom:1px solid var(--border);">
                            ${p.description || 'Premium quality clothing.'}
                        </p>
                        <div style="margin-bottom:24px;font-size:12px;font-weight:600;letter-spacing:0.5px;
                                    color:${p.stock > 0 ? 'var(--success)' : 'var(--danger)'}">
                            ${p.stock > 0 ? `✓ In Stock &nbsp;(${p.stock} available)` : '✕ Out of Stock'}
                        </div>
                        <button class="btn btn-dark btn-full"
                            onclick="addToCart(${pid},'${nameEsc}',${finalPrice},'${p.category}','${imgUrlEsc}');closeModal('quickViewModal')"
                            ${p.stock === 0 ? 'disabled style="opacity:0.5;cursor:not-allowed;"' : ''}>
                            ${p.stock === 0 ? 'Out of Stock' : '+ Add to Cart'}
                        </button>
                        <button class="btn btn-outline-dark btn-full" style="margin-top:10px;"
                            onclick="goToProduct(${pid});closeModal('quickViewModal')">
                            View Full Details
                        </button>
                    </div>
                </div>
            `;
            openModal('quickViewModal');
        })
        .catch(() => showToast('Could not load product'));
}

// ===== STATUS BADGE =====
function statusBadge(status) {
    const map = {
        'Confirmed':  'badge-gold',
        'Processing': 'badge-dark',
        'Shipped':    'badge-info',
        'Delivered':  'badge-success',
        'Cancelled':  'badge-danger'
    };
    return `<span class="badge ${map[status] || 'badge-dark'}">${status || 'Confirmed'}</span>`;
}

// ===== MODAL =====
function openModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.add('open');
}

function closeModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.remove('open');
}

// ===== ALERT =====
function showAlert(id, msg, type = 'error') {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.style.display = 'block';
    setTimeout(() => el.style.display = 'none', 5000);
}

// ===== TOAST =====
function showToast(msg) {
    let t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        document.body.appendChild(t);
    }
    t.textContent = msg;
    t.classList.add('show');
    clearTimeout(t._timer);
    t._timer = setTimeout(() => t.classList.remove('show'), 3000);
}
