
const API = "/api";

async function apiGet(path) {
    const response = await fetch(`${API}${path}`);

    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }

    return response.json();
}

async function apiPost(path, data) {
    const response = await fetch(`${API}${path}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    const body = await response.text();

    if (!response.ok) {
        throw new Error(body || `Request failed: ${response.status}`);
    }

    return body ? JSON.parse(body) : null;
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

function showConnection(message, connected) {
    const status = document.getElementById("connectionStatus");
    status.textContent = message;
    status.style.color = connected ? "#65df9b" : "#ff777e";

    const dot = document.querySelector(".live-dot");
    dot.style.background = connected ? "#65df9b" : "#ff3b45";
}

async function loadDashboard() {
    try {
        const [teams, drivers, cars, championships] = await Promise.all([
            apiGet("/teams"),
            apiGet("/drivers"),
            apiGet("/cars"),
            apiGet("/championships")
        ]);

        document.getElementById("teamCount").textContent = teams.length;
        document.getElementById("driverCount").textContent = drivers.length;
        document.getElementById("carCount").textContent = cars.length;
        document.getElementById("championshipCount").textContent =
            championships.length;

        showConnection("BACKEND CONNECTED", true);

        await Promise.all([loadRaces(), loadStandings()]);
    } catch (error) {
        showConnection("BACKEND DISCONNECTED", false);
        document.getElementById("raceList").innerHTML =
            '<p class="muted">Could not load race events. Check that RaceTrack is running.</p>';
        document.getElementById("standingsList").innerHTML =
            '<p class="muted">Could not load standings.</p>';
        console.error(error);
    }
}

async function loadRaces() {
    const container = document.getElementById("raceList");

    try {
        const races = await apiGet("/races");

        if (!races.length) {
            container.innerHTML = '<p class="muted">No race events registered yet.</p>';
            return;
        }

        container.innerHTML = `
            <table class="table">
                <thead>
                    <tr>
                        <th>RACE</th>
                        <th>CIRCUIT</th>
                        <th>DATE</th>
                        <th>STATUS</th>
                    </tr>
                </thead>
                <tbody>
                    ${races.map(race => `
                        <tr>
                            <td class="fw-bold">${escapeHtml(race.name)}</td>
                            <td>${escapeHtml(race.circuit || "—")}</td>
                            <td>${escapeHtml(race.raceDate || "—")}</td>
                            <td><span class="status-pill">${escapeHtml(race.status || "UPCOMING")}</span></td>
                        </tr>
                    `).join("")}
                </tbody>
            </table>`;
    } catch (error) {
        container.innerHTML = '<p class="muted">Unable to load races.</p>';
        console.error(error);
    }
}

async function loadStandings() {
    const container = document.getElementById("standingsList");

    try {
        const standings = await apiGet("/standings");

        if (!standings.length) {
            container.innerHTML = '<p class="muted">No race results recorded yet.</p>';
            return;
        }

        container.innerHTML = `
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>POS.</th>
                            <th>DRIVER</th>
                            <th>TEAM</th>
                            <th>PTS</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${standings.map(row => `
                            <tr>
                                <td class="fw-bold">${escapeHtml(row.position)}</td>
                                <td>${escapeHtml(row.driverName)}</td>
                                <td>${escapeHtml(row.teamName)}</td>
                                <td class="fw-bold">${escapeHtml(row.totalPoints)}</td>
                            </tr>
                        `).join("")}
                    </tbody>
                </table>
            </div>`;
    } catch (error) {
        container.innerHTML = '<p class="muted">Unable to load standings.</p>';
        console.error(error);
    }
}

function showSection(section) {
    const panel = document.getElementById("managementSection");
    const title = document.getElementById("managementTitle");
    const content = document.getElementById("managementContent");

    const titles = {
        teams: "Team Management",
        drivers: "Driver Management",
        cars: "Car Management",
        championships: "Championship Management"
    };

    title.textContent = titles[section] || "Management";
    panel.hidden = false;

    if (section === "teams") {
        content.innerHTML = `
            <form id="teamForm" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Team name</label>
                    <input class="form-control" name="name" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Country</label>
                    <input class="form-control" name="country" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Team principal</label>
                    <input class="form-control" name="teamPrincipal">
                </div>
                <div class="col-12">
                    <button class="btn btn-accent" type="submit">Register Team</button>
                </div>
                <div class="col-12" id="formMessage"></div>
            </form>`;
        document.getElementById("teamForm").addEventListener("submit", addTeam);
    } else if (section === "drivers") {
        showDriverForm(content);
    } else if (section === "cars") {
        showCarForm(content);
    } else if (section === "championships") {
        content.innerHTML = `
            <form id="championshipForm" class="row g-3">
                <div class="col-md-5">
                    <label class="form-label">Championship name</label>
                    <input class="form-control" name="name" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Season year</label>
                    <input class="form-control" name="seasonYear" type="number"
                           min="1950" max="2100" value="2026" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Status</label>
                    <select class="form-select" name="status">
                        <option>UPCOMING</option>
                        <option>ONGOING</option>
                        <option>COMPLETED</option>
                    </select>
                </div>
                <div class="col-12">
                    <button class="btn btn-accent" type="submit">Create Championship</button>
                </div>
                <div class="col-12" id="formMessage"></div>
            </form>`;
        document.getElementById("championshipForm")
            .addEventListener("submit", addChampionship);
    }

    panel.scrollIntoView({ behavior: "smooth", block: "start" });
}

async function showDriverForm(content) {
    try {
        const teams = await apiGet("/teams");

        content.innerHTML = `
            <form id="driverForm" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">Driver name</label>
                    <input class="form-control" name="name" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Nationality</label>
                    <input class="form-control" name="nationality" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Racing number</label>
                    <input class="form-control" name="racingNumber" type="number"
                           min="1" max="99" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Team</label>
                    <select class="form-select" name="teamId" required>
                        <option value="">Select team</option>
                        ${teams.map(team => `
                            <option value="${team.id}">${escapeHtml(team.name)}</option>
                        `).join("")}
                    </select>
                </div>
                <div class="col-12">
                    <button class="btn btn-accent" type="submit">Register Driver</button>
                </div>
                <div class="col-12" id="formMessage"></div>
            </form>`;

        document.getElementById("driverForm")
            .addEventListener("submit", addDriver);
    } catch (error) {
        content.innerHTML = '<p class="muted">Could not load teams. Check the backend connection.</p>';
    }
}

async function showCarForm(content) {
    try {
        const teams = await apiGet("/teams");

        content.innerHTML = `
            <form id="carForm" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Car model</label>
                    <input class="form-control" name="model" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Engine</label>
                    <input class="form-control" name="engine">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Season year</label>
                    <input class="form-control" name="seasonYear" type="number"
                           min="1950" max="2100" value="2026">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Team</label>
                    <select class="form-select" name="teamId" required>
                        <option value="">Select team</option>
                        ${teams.map(team => `
                            <option value="${team.id}">${escapeHtml(team.name)}</option>
                        `).join("")}
                    </select>
                </div>
                <div class="col-12">
                    <button class="btn btn-accent" type="submit">Register Car</button>
                </div>
                <div class="col-12" id="formMessage"></div>
            </form>`;

        document.getElementById("carForm")
            .addEventListener("submit", addCar);
    } catch (error) {
        content.innerHTML = '<p class="muted">Could not load teams. Check the backend connection.</p>';
    }
}

async function addTeam(event) {
    event.preventDefault();
    const form = event.currentTarget;

    try {
        await apiPost("/teams", {
            name: form.name.value.trim(),
            country: form.country.value.trim(),
            teamPrincipal: form.teamPrincipal.value.trim()
        });

        form.reset();
        showMessage("Team registered successfully.", true);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, false);
    }
}

async function addDriver(event) {
    event.preventDefault();
    const form = event.currentTarget;

    try {
        await apiPost("/drivers", {
            name: form.name.value.trim(),
            nationality: form.nationality.value.trim(),
            racingNumber: Number(form.racingNumber.value),
            team: { id: Number(form.teamId.value) }
        });

        form.reset();
        showMessage("Driver registered successfully.", true);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, false);
    }
}

async function addCar(event) {
    event.preventDefault();
    const form = event.currentTarget;

    try {
        await apiPost("/cars", {
            model: form.model.value.trim(),
            engine: form.engine.value.trim(),
            seasonYear: form.seasonYear.value
                ? Number(form.seasonYear.value) : null,
            team: { id: Number(form.teamId.value) }
        });

        form.reset();
        showMessage("Car registered successfully.", true);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, false);
    }
}

async function addChampionship(event) {
    event.preventDefault();
    const form = event.currentTarget;

    try {
        await apiPost("/championships", {
            name: form.name.value.trim(),
            seasonYear: Number(form.seasonYear.value),
            status: form.status.value
        });

        form.reset();
        showMessage("Championship created successfully.", true);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, false);
    }
}

function showMessage(message, success) {
    const container = document.getElementById("formMessage");
    if (!container) return;

    container.innerHTML = `
        <div class="alert ${success ? "alert-success" : "alert-danger"} mt-2">
            ${escapeHtml(message)}
        </div>`;
}

function closeSection() {
    document.getElementById("managementSection").hidden = true;
}

document.addEventListener("DOMContentLoaded", loadDashboard);
