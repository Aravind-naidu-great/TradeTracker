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
}
