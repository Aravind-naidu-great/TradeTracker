const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

function animateCount(element) {
    const target = Number(element.dataset.count);
    const hasCurrency = element.textContent.trim().startsWith("$");
    const duration = prefersReducedMotion ? 0 : 1200;
    const startTime = performance.now();

    function render(now) {
        const progress = duration === 0 ? 1 : Math.min((now - startTime) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        const value = Math.round(target * eased);

        element.textContent = hasCurrency ? `$${value}M` : value.toLocaleString();

        if (progress < 1) {
            requestAnimationFrame(render);
        }
    }

    requestAnimationFrame(render);
}

document.querySelectorAll("[data-count]").forEach(animateCount);

document.querySelectorAll(".workflow-card").forEach((card) => {
    card.addEventListener("click", () => {
        document.querySelectorAll(".workflow-card").forEach((item) => {
            item.classList.toggle("active", item === card);
        });
    });
});

async function submitAuthForm(form, messageElement) {
    const formData = new FormData(form);
    const payload = Object.fromEntries(formData.entries());

    messageElement.className = "auth-message";
    messageElement.textContent = "Connecting to TradeTracker...";

    try {
        const response = await fetch(form.action, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.message || data.error || "Request failed. Please check your details.");
        }

        messageElement.classList.add("success");
        messageElement.textContent = data.message || "Success.";

        if (form.id === "signupForm") {
            form.reset();
            setTimeout(() => {
                window.location.href = "signin.html";
            }, 1200);
        } else if (form.id === "signinForm") {
            localStorage.setItem("tradeTrackerUser", JSON.stringify(data));
            setTimeout(() => {
                window.location.href = "dashboard.html";
            }, 600);
        }
    } catch (error) {
        messageElement.classList.add("error");
        messageElement.textContent = error.message;
    }
}

const signinForm = document.querySelector("#signinForm");
const signupForm = document.querySelector("#signupForm");

if (signinForm) {
    signinForm.addEventListener("submit", (event) => {
        event.preventDefault();
        submitAuthForm(signinForm, document.querySelector("#signinMessage"));
    });
}

if (signupForm) {
    signupForm.addEventListener("submit", (event) => {
        event.preventDefault();
        submitAuthForm(signupForm, document.querySelector("#signupMessage"));
    });
}

const userMenu = document.querySelector(".user-menu");
const userMenuButton = document.querySelector(".user-menu-button");
const dashboardUserName = document.querySelector("#dashboardUserName");
const userInitial = document.querySelector("#userInitial");
const logoutButton = document.querySelector("#logoutButton");

if (userMenu && userMenuButton) {
    const savedUser = JSON.parse(localStorage.getItem("tradeTrackerUser") || "{}");
    const displayName = savedUser.businessName || savedUser.email || "Customer";

    dashboardUserName.textContent = displayName;
    userInitial.textContent = displayName.trim().charAt(0).toUpperCase() || "U";

    userMenuButton.addEventListener("click", () => {
        const isOpen = userMenu.classList.toggle("open");
        userMenuButton.setAttribute("aria-expanded", String(isOpen));
    });

    document.addEventListener("click", (event) => {
        if (!userMenu.contains(event.target)) {
            userMenu.classList.remove("open");
            userMenuButton.setAttribute("aria-expanded", "false");
        }
    });

    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            localStorage.removeItem("tradeTrackerUser");
            window.location.href = "signin.html";
        });
    }
}

const appSearchForm = document.querySelector("#appSearchForm");
const appSearchInput = document.querySelector("#appSearchInput");
const appSearchResults = document.querySelector("#appSearchResults");

function escapeHtml(value = "") {
    return String(value).replace(/[&<>"']/g, (character) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "\"": "&quot;",
        "'": "&#39;",
    })[character]);
}

function renderSearchResults(data) {
    if (!appSearchResults) {
        return;
    }

    const results = data.results || [];

    if (results.length === 0) {
        appSearchResults.innerHTML = `<p>${escapeHtml(data.answer || "No matches found yet.")}</p>`;
        return;
    }

    const resultItems = results.map((result) => `
        <a class="search-result-item" href="${escapeHtml(result.url)}">
            <span>${escapeHtml(result.type)}</span>
            <strong>${escapeHtml(result.title)}</strong>
            <small>${escapeHtml(result.description)}</small>
        </a>
    `).join("");

    appSearchResults.innerHTML = `
        <p>${escapeHtml(data.answer)}</p>
        <div class="search-result-list">${resultItems}</div>
    `;
}

async function searchApplication(query) {
    if (!appSearchResults) {
        return;
    }

    if (!query.trim()) {
        appSearchResults.innerHTML = `<p>Try natural language like &quot;show active shipments&quot; or &quot;where is revenue?&quot;.</p>`;
        return;
    }

    appSearchResults.innerHTML = "<p>Searching TradeTracker...</p>";

    try {
        const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.message || data.error || "Search failed.");
        }

        renderSearchResults(data);
    } catch (error) {
        appSearchResults.innerHTML = `<p>${escapeHtml(error.message)}</p>`;
    }
}

if (appSearchForm && appSearchInput) {
    let searchDelay;

    appSearchInput.addEventListener("input", () => {
        window.clearTimeout(searchDelay);
        searchDelay = window.setTimeout(() => searchApplication(appSearchInput.value), 250);
    });

    appSearchForm.addEventListener("submit", (event) => {
        event.preventDefault();
        window.clearTimeout(searchDelay);
        searchApplication(appSearchInput.value);
    });
}

const buyerRequirementsGrid = document.querySelector("#buyerRequirementsGrid");
const requirementFeed = document.querySelector("#requirementFeed");
const requirementForm = document.querySelector("#requirementForm");
const requirementCategory = document.querySelector("#requirementCategory");
const requirementSubCategory = document.querySelector("#requirementSubCategory");
const requirementMessage = document.querySelector("#requirementMessage");
const requirementCsvForm = document.querySelector("#requirementCsvForm");
const requirementCsvMessage = document.querySelector("#requirementCsvMessage");
const sellerGrid = document.querySelector("#sellerGrid");
const sellerForm = document.querySelector("#sellerForm");
const sellerMessage = document.querySelector("#sellerMessage");
const messageThread = document.querySelector("#messageThread");
const chatForm = document.querySelector("#chatForm");
const chatMessage = document.querySelector("#chatMessage");

function formatRequirementDate(value) {
    if (!value) {
        return "Date not listed";
    }

    return new Intl.DateTimeFormat("en", {
        month: "short",
        day: "numeric",
        year: "numeric",
    }).format(new Date(`${value}T00:00:00`));
}

function renderRequirements(requirements, container) {
    if (!container) {
        return;
    }

    if (requirements.length === 0) {
        container.innerHTML = `
            <article class="requirement-card loading-card">
                <p>No requirements are posted yet.</p>
            </article>
        `;
        return;
    }

    container.innerHTML = requirements.map((requirement) => `
        <article class="requirement-card">
            <div class="requirement-topline">
                <span>${escapeHtml(requirement.requirementType || "Need")}</span>
                <strong>${escapeHtml(requirement.status)}</strong>
            </div>
            <h3>${escapeHtml(requirement.productName)}</h3>
            <p>${escapeHtml(requirement.qualitySpecs)}</p>
            <dl class="requirement-details">
                <div>
                    <dt>Business</dt>
                    <dd>${escapeHtml(requirement.buyerName)}</dd>
                </div>
                <div>
                    <dt>Category</dt>
                    <dd>${escapeHtml(requirement.category)} / ${escapeHtml(requirement.subCategory || requirement.productName)}</dd>
                </div>
                <div>
                    <dt>Country</dt>
                    <dd>${escapeHtml(requirement.country)}</dd>
                </div>
                <div>
                    <dt>Required quantity</dt>
                    <dd>${escapeHtml(requirement.requiredQuantity)}</dd>
                </div>
                <div>
                    <dt>Payment type</dt>
                    <dd>${escapeHtml(requirement.paymentType)}</dd>
                </div>
                <div>
                    <dt>Date of requirement</dt>
                    <dd>${escapeHtml(formatRequirementDate(requirement.requirementDate))}</dd>
                </div>
                <div>
                    <dt>Target price</dt>
                    <dd>${escapeHtml(requirement.targetPrice)}</dd>
                </div>
                <div>
                    <dt>Delivery port</dt>
                    <dd>${escapeHtml(requirement.deliveryPort)}</dd>
                </div>
                <div>
                    <dt>Incoterm</dt>
                    <dd>${escapeHtml(requirement.incoterm)}</dd>
                </div>
                <div>
                    <dt>Shipment type</dt>
                    <dd>${escapeHtml(requirement.shipmentType || "Not listed")}</dd>
                </div>
            </dl>
            ${requirement.notes ? `<p>${escapeHtml(requirement.notes)}</p>` : ""}
        </article>
    `).join("");
}

async function loadBuyerRequirements() {
    if (!buyerRequirementsGrid) {
        return;
    }

    try {
        const response = await fetch("/api/buyer-requirements");
        const requirements = await response.json().catch(() => []);

        if (!response.ok) {
            throw new Error("Could not load buyer requirements.");
        }

        renderRequirements(requirements, buyerRequirementsGrid);
    } catch (error) {
        buyerRequirementsGrid.innerHTML = `
            <article class="requirement-card loading-card">
                <p>${escapeHtml(error.message)}</p>
            </article>
        `;
    }
}

loadBuyerRequirements();

async function loadRequirementCategories() {
    if (!requirementCategory || !requirementSubCategory) {
        return;
    }

    const response = await fetch("/api/requirement-categories");
    const categories = await response.json();
    const categoryNames = Object.keys(categories);

    function renderSubCategories(categoryName) {
        requirementSubCategory.innerHTML = categories[categoryName]
            .map((subCategory) => `<option>${escapeHtml(subCategory)}</option>`)
            .join("");
    }

    requirementCategory.innerHTML = categoryNames
        .map((category) => `<option>${escapeHtml(category)}</option>`)
        .join("");
    renderSubCategories(categoryNames[0]);

    requirementCategory.addEventListener("change", () => {
        renderSubCategories(requirementCategory.value);
    });
}

async function loadRequirementFeed() {
    if (!requirementFeed) {
        return;
    }

    try {
        const response = await fetch("/api/buyer-requirements");
        const requirements = await response.json().catch(() => []);

        if (!response.ok) {
            throw new Error("Could not load requirements.");
        }

        renderRequirements(requirements, requirementFeed);
    } catch (error) {
        requirementFeed.innerHTML = `
            <article class="requirement-card loading-card">
                <p>${escapeHtml(error.message)}</p>
            </article>
        `;
    }
}

if (requirementForm) {
    const savedUser = JSON.parse(localStorage.getItem("tradeTrackerUser") || "{}");
    const businessInput = requirementForm.querySelector("[name='buyerName']");

    if (businessInput && savedUser.businessName) {
        businessInput.value = savedUser.businessName;
    }

    requirementForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        requirementMessage.className = "auth-message";
        requirementMessage.textContent = "Posting requirement...";

        try {
            const formData = new FormData(requirementForm);
            const payload = Object.fromEntries(formData.entries());
            const response = await fetch("/api/buyer-requirements", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });
            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(data.message || "Could not post requirement.");
            }

            requirementMessage.classList.add("success");
            requirementMessage.textContent = "Requirement posted.";
            requirementForm.reset();
            await loadRequirementCategories();
            await loadRequirementFeed();
        } catch (error) {
            requirementMessage.classList.add("error");
            requirementMessage.textContent = error.message;
        }
    });
}

async function uploadCsv(form, endpoint, messageElement, afterImport) {
    messageElement.className = "auth-message";
    messageElement.textContent = "Importing CSV...";

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            body: new FormData(form),
        });
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.message || "CSV import failed.");
        }

        messageElement.classList.add("success");
        messageElement.textContent = `${data.importedRows || 0} rows imported.`;
        form.reset();
        await afterImport();
    } catch (error) {
        messageElement.classList.add("error");
        messageElement.textContent = error.message;
    }
}

if (requirementCsvForm && requirementCsvMessage) {
    requirementCsvForm.addEventListener("submit", (event) => {
        event.preventDefault();
        uploadCsv(requirementCsvForm, "/api/buyer-requirements/import-csv", requirementCsvMessage, loadRequirementFeed);
    });
}

async function loadSellers() {
    if (!sellerGrid) {
        return;
    }

    try {
        const response = await fetch("/api/sellers");
        const sellers = await response.json().catch(() => []);

        if (!response.ok) {
            throw new Error("Could not load sellers.");
        }

        sellerGrid.innerHTML = sellers.map((seller) => `
            <article class="seller-card">
                ${seller.productImageUrl ? `<img class="seller-product-image" src="${escapeHtml(seller.productImageUrl)}" alt="${escapeHtml(seller.products)}">` : ""}
                <div class="requirement-topline">
                    <span>${escapeHtml(seller.category)}</span>
                    <strong>${seller.verified ? "Verified" : "Pending"}</strong>
                </div>
                <h3>${escapeHtml(seller.businessName)}</h3>
                <p>${escapeHtml(seller.products)}</p>
                <dl class="requirement-details">
                    <div><dt>Country</dt><dd>${escapeHtml(seller.country)}</dd></div>
                    <div><dt>Sub category</dt><dd>${escapeHtml(seller.subCategory)}</dd></div>
                    <div><dt>Available</dt><dd>${escapeHtml(seller.availableQuantity)}</dd></div>
                    <div><dt>Price</dt><dd>${escapeHtml(seller.priceRange)}</dd></div>
                    <div><dt>Shipment</dt><dd>${escapeHtml(seller.shipmentTypes)}</dd></div>
                    <div><dt>Payment</dt><dd>${escapeHtml(seller.paymentTerms)}</dd></div>
                    <div><dt>Certifications</dt><dd>${escapeHtml(seller.certifications)}</dd></div>
                    <div><dt>Response</dt><dd>${escapeHtml(seller.responseTime)}</dd></div>
                </dl>
                <a class="secondary-button seller-chat-link" href="chat.html">Message seller</a>
            </article>
        `).join("");
    } catch (error) {
        sellerGrid.innerHTML = `
            <article class="seller-card loading-card">
                <p>${escapeHtml(error.message)}</p>
            </article>
        `;
    }
}

if (sellerForm && sellerMessage) {
    const savedUser = JSON.parse(localStorage.getItem("tradeTrackerUser") || "{}");
    const businessInput = sellerForm.querySelector("[name='businessName']");

    if (businessInput && savedUser.businessName) {
        businessInput.value = savedUser.businessName;
    }

    sellerForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        sellerMessage.className = "auth-message";
        sellerMessage.textContent = "Posting seller listing...";

        try {
            const response = await fetch("/api/sellers", {
                method: "POST",
                body: new FormData(sellerForm),
            });
            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(data.message || "Could not post seller listing.");
            }

            sellerMessage.classList.add("success");
            sellerMessage.textContent = "Seller listing posted.";
            sellerForm.reset();
            await loadSellers();
        } catch (error) {
            sellerMessage.classList.add("error");
            sellerMessage.textContent = error.message;
        }
    });
}

function formatMessageTime(value) {
    if (!value) {
        return "";
    }

    return new Intl.DateTimeFormat("en", {
        hour: "numeric",
        minute: "2-digit",
    }).format(new Date(value));
}

async function loadChatMessages() {
    if (!messageThread) {
        return;
    }

    try {
        const response = await fetch("/api/chat/messages");
        const messages = await response.json().catch(() => []);

        if (!response.ok) {
            throw new Error("Could not load chat messages.");
        }

        messageThread.innerHTML = messages.map((message, index) => `
            <article class="message-bubble ${index % 2 === 0 ? "incoming" : "outgoing"}">
                <span>${escapeHtml(message.senderBusinessName)} to ${escapeHtml(message.recipientBusinessName)}</span>
                <p>${escapeHtml(message.messageText)}</p>
                <small>${escapeHtml(message.conversationName)} · ${escapeHtml(formatMessageTime(message.sentAt))}</small>
            </article>
        `).join("");
        messageThread.scrollTop = messageThread.scrollHeight;
    } catch (error) {
        messageThread.innerHTML = `<p>${escapeHtml(error.message)}</p>`;
    }
}

if (chatForm) {
    const savedUser = JSON.parse(localStorage.getItem("tradeTrackerUser") || "{}");
    const senderInput = chatForm.querySelector("[name='senderBusinessName']");

    if (senderInput && savedUser.businessName) {
        senderInput.value = savedUser.businessName;
    }

    chatForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        chatMessage.className = "auth-message";
        chatMessage.textContent = "Sending...";

        try {
            const payload = Object.fromEntries(new FormData(chatForm).entries());
            const response = await fetch("/api/chat/messages", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });
            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(data.message || "Could not send message.");
            }

            chatMessage.classList.add("success");
            chatMessage.textContent = "Message sent.";
            chatForm.querySelector("[name='messageText']").value = "";
            await loadChatMessages();
        } catch (error) {
            chatMessage.classList.add("error");
            chatMessage.textContent = error.message;
        }
    });
}

loadRequirementCategories();
loadRequirementFeed();
loadSellers();
loadChatMessages();
