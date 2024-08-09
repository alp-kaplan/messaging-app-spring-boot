const apiUrl = "http://localhost:8080/api"; // Base URL for the API.
let authToken = ""; // Authentication token for the user.
let isAdmin = false; // Boolean to check if the user is an admin.
const pageSize = 10; // Number of items per page.

/**
 * Handles the login process for the user.
 * Sends a POST request with the username and password to the login endpoint.
 * If successful, stores the auth token and displays the appropriate menu.
 */
function login() {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    if (!username || !password) {
        alert('All fields are required!');
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", `${apiUrl}/user/login`, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const token = xhr.responseText;
                authToken = token;
                localStorage.setItem("authToken", authToken);
                const decodedToken = parseJwt(token);
                isAdmin = decodedToken.isAdmin;
                localStorage.setItem("isAdmin", isAdmin);

                showMenu(decodedToken.isAdmin);
                getInbox();
            } else {
                alert("Login failed");
            }
        }
    };
    xhr.send(JSON.stringify({ username, password }));
}

/**
 * Handles the logout process for the user.
 * Sends a POST request to the logout endpoint and clears the stored auth token.
 */
function logout() {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", `${apiUrl}/user/logout`, true);
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            authToken = "";
            isAdmin = false;
            localStorage.removeItem("authToken");
            localStorage.removeItem("isAdmin");
            document.querySelector(".login-form").style.display = "block";
            document.querySelector(".message-app").style.display = "none";
            document.querySelector(".navigation").style.display = "none";
        }
    };
    xhr.send();
}

/**
 * Initializes the application on window load.
 * Checks if there is a stored auth token and sets the user state accordingly.
 */
window.onload = function() {
    const storedToken = localStorage.getItem("authToken");
    if (storedToken) {
        authToken = storedToken;
        const decodedToken = parseJwt(authToken);
        isAdmin = decodedToken.isAdmin;
        showMenu(isAdmin);
        getInbox();
    } else {
        document.querySelector(".login-form").style.display = "block";
    }
};

/**
 * Displays the menu based on the user's admin status.
 * @param {boolean} isAdmin - Indicates if the user is an admin.
 */
function showMenu(isAdmin) {
    document.querySelector(".login-form").style.display = "none";
    document.querySelector(".message-app").style.display = "block";
    document.querySelector(".navigation").style.display = "flex";

    if (isAdmin) {
        document.getElementById("list-users-btn").style.display = "block";
        document.getElementById("add-user-btn").style.display = "block";
        document.getElementById("user-management").style.display = "block";
    } else {
        document.getElementById("list-users-btn").style.display = "none";
        document.getElementById("add-user-btn").style.display = "none";
        document.getElementById("user-management").style.display = "none";
    }
}

/**
 * Hides all sections of the UI.
 */
function hideAllSections() {
    document.getElementById("send-message-section").style.display = "none";
    document.getElementById("add-user-section").style.display = "none";
    document.getElementById("message-list").style.display = "none";
    document.getElementById("user-list").style.display = "none";
    document.getElementById("list-users-section").style.display = "none";
    document.getElementById("pagination").style.display = "none";
    document.getElementById("user-pagination").style.display = "none";
    document.getElementById("inbox-sort-controls").style.display = "none";
    document.getElementById("outbox-sort-controls").style.display = "none";
    document.getElementById("inbox-header").style.display = "none";
    document.getElementById("outbox-header").style.display = "none";
    document.getElementById("inbox-filter-controls").style.display = "none";
    document.getElementById("outbox-filter-controls").style.display = "none";
}

/**
 * Handles unauthorized requests by alerting the user and logging them out.
 * @param {XMLHttpRequest} xhr - The XMLHttpRequest object.
 */
function handleUnauthorized(xhr) {
    if (xhr.status === 401) {
        alert("Unauthorized!");
        logout();
    }
}

/**
 * Fetches the user's inbox messages from the server.
 * @param {number} [page=0] - The page number to fetch.
 */
function getInbox(page = 0) {
    hideAllSections();
    document.getElementById("inbox-header").style.display = "block";
    document.getElementById("inbox-sort-controls").style.display = "block";
    document.getElementById("inbox-filter-controls").style.display = "block";

    const field = document.getElementById("inbox-filter-category").value;
    const value = document.getElementById("inbox-filter-value").value;

    const xhr = new XMLHttpRequest();

    if(field && value){
        xhr.open("GET", `${apiUrl}/message?inout=in&page=${page}&size=${pageSize}&field=${field}&value=${value}`, true);
    }
    else{
        xhr.open("GET", `${apiUrl}/message?inout=in&page=${page}&size=${pageSize}`, true);
    }
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                if(xhr.status === 204) {
                    alert("No messages found.");
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                const messages = response.content;
                sortMessages(messages, 'inbox-sort-category', 'inbox-sort-order');
                setupPagination(response, "getInbox");
            } else {
                handleUnauthorized(xhr);
                alert("Failed to get inbox.");
            }
        }
    };
    xhr.send();
}

/**
 * Fetches the user's outbox messages from the server.
 * @param {number} [page=0] - The page number to fetch.
 */
function getOutbox(page = 0) {
    hideAllSections();
    document.getElementById("outbox-header").style.display = "block";
    document.getElementById("outbox-sort-controls").style.display = "block";
    document.getElementById("outbox-filter-controls").style.display = "block";

    const field = document.getElementById("outbox-filter-category").value;
    const value = document.getElementById("outbox-filter-value").value;

    const xhr = new XMLHttpRequest();

    if(field && value){
        xhr.open("GET", `${apiUrl}/message?inout=out&page=${page}&size=${pageSize}&field=${field}&value=${value}`, true);
    }
    else{
        xhr.open("GET", `${apiUrl}/message?inout=out&page=${page}&size=${pageSize}`, true);
    }
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                if(xhr.status === 204) {
                    alert("No messages found.");
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                const messages = response.content;
                sortMessages(messages, 'outbox-sort-category', 'outbox-sort-order');
                setupPagination(response, "getOutbox");
            } else {
                handleUnauthorized(xhr);
                alert("Failed to get outbox.");
            }
        }
    };
    xhr.send();
}

/**
 * Sorts the messages based on the specified category and order.
 * @param {Array} messages - The list of messages to sort.
 * @param {string} categoryElementId - The ID of the element that specifies the sort category.
 * @param {string} orderElementId - The ID of the element that specifies the sort order.
 */
function sortMessages(messages, categoryElementId, orderElementId) {
    const category = document.getElementById(categoryElementId).value;
    const order = document.getElementById(orderElementId).value;

    messages.sort((a, b) => {
        let valA = a[category].toLowerCase();
        let valB = b[category].toLowerCase();

        if (category === "timestamp") {
            valA = new Date(a.timestamp);
            valB = new Date(b.timestamp);
        }

        if (valA < valB) {
            return order === 'asc' ? -1 : 1;
        }
        if (valA > valB) {
            return order === 'asc' ? 1 : -1;
        }
        return 0;
    });

    displayMessages(messages);
}

/**
 * Displays the sorted and filtered messages in the UI.
 * @param {Array} messages - The list of messages to display.
 */
function displayMessages(messages) {
    const messageList = document.getElementById("message-list");
    messageList.innerHTML = "";
    const table = document.createElement("table");
    table.innerHTML = `
        <thead>
            <tr>
                <th>From</th>
                <th>To</th>
                <th>Content</th>
                <th>Timestamp</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
        `;
    const tbody = table.querySelector("tbody");
    messages.forEach(message => {
        const date = new Date(message.timestamp);
        const formattedTimestamp = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${message.sender}</td>
            <td>${message.receiver}</td>
            <td>${message.content}</td>
            <td>${formattedTimestamp}</td>
            `;
        tbody.appendChild(row);
    });
    messageList.appendChild(table);
    messageList.style.display = "block";
}

/**
 * Fetches the list of users from the server.
 * @param {number} [page=0] - The page number to fetch.
 */
function listUsers(page = 0) {
    const field = document.getElementById("user-filter-category").value;
    const value = document.getElementById("user-filter-value").value;

    const xhr = new XMLHttpRequest();

    if(field && value){
        xhr.open("GET", `${apiUrl}/user?page=${page}&size=${pageSize}&field=${field}&value=${value}`, true);
    }
    else{
        xhr.open("GET", `${apiUrl}/user?page=${page}&size=${pageSize}`, true);
    }
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                if(xhr.status === 204) {
                    alert("No users found.");
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                const users = response.content;
                sortUsers(users);
                setupUserPagination(response);
            } else {
                handleUnauthorized(xhr);
                alert("Failed to list users.");
            }
        }
    };
    xhr.send();
}

/**
 * Sorts the list of users based on the selected category and order.
 * @param {Array} users - The list of users to sort.
 */
function sortUsers(users) {
    const category = document.getElementById('user-sort-category').value;
    const order = document.getElementById('user-sort-order').value;

    users.sort((a, b) => {
        let valA = a[category].toLowerCase();
        let valB = b[category].toLowerCase();

        if (category === "birthdate") {
            valA = new Date(a.birthdate);
            valB = new Date(b.birthdate);
        }

        if (valA < valB) {
            return order === 'asc' ? -1 : 1;
        }
        if (valA > valB) {
            return order === 'asc' ? 1 : -1;
        }
        return 0;
    });

    displayUsers(users);
}

/**
 * Displays the list of users in a table format.
 * @param {Array} users - The list of users to display.
 */
function displayUsers(users) {
    const userList = document.getElementById("user-list");
    userList.innerHTML = "";
    const table = document.createElement("table");
    table.innerHTML = `
        <thead>
            <tr>
                <th>Select</th>
                <th>Username</th>
                <th>Name</th>
                <th>Surname</th>
                <th>Birthdate</th>
                <th>Gender</th>
                <th>Email</th>
                <th>Location</th>
                <th>Admin</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
        `;
    const tbody = table.querySelector("tbody");
    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td><input type="checkbox" name="user-select" value="${user.username}"></td>
            <td>${user.username}</td>
            <td>${user.name}</td>
            <td>${user.surname}</td>
            <td>${user.birthdate}</td>
            <td>${user.gender}</td>
            <td>${user.email}</td>
            <td>${user.location}</td>
            <td>${user.admin}</td>
            `;
        tbody.appendChild(row);
    });
    userList.appendChild(table);
    userList.style.display = "block";
}

/**
 * Sets up pagination for the displayed data.
 * @param {Object} response - The response object containing pagination info.
 * @param {string} fetchFunctionName - The name of the function to call for fetching data.
 */
function setupPagination(response, fetchFunctionName) {
    const pagination = document.getElementById("pagination");
    pagination.style.display = "flex";
    const pageSelect = document.getElementById("page-select");
    pageSelect.innerHTML = "";

    for (let i = 0; i < response.totalPages; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.text = i + 1;
        pageSelect.appendChild(option);
    }

    pageSelect.value = response.number;
    pageSelect.setAttribute("onchange", `${fetchFunctionName}(this.value)`);
}

/**
 * Navigates to the previous page in the pagination.
 */
function previousPage() {
    const pageSelect = document.getElementById("page-select");
    const currentPage = parseInt(pageSelect.value);
    if (currentPage > 0) {
        pageSelect.value = currentPage - 1;
        pageSelect.dispatchEvent(new Event('change'));
    }
}

/**
 * Navigates to the next page in the pagination.
 */
function nextPage() {
    const pageSelect = document.getElementById("page-select");
    const currentPage = parseInt(pageSelect.value);
    if (currentPage < pageSelect.options.length - 1) {
        pageSelect.value = currentPage + 1;
        pageSelect.dispatchEvent(new Event('change'));
    }
}

/**
 * Sets up pagination for the list of users.
 * @param {Object} response - The response object containing pagination info.
 */
function setupUserPagination(response) {
    const userPagination = document.getElementById("user-pagination");
    userPagination.style.display = "flex";
    const userPageSelect = document.getElementById("user-page-select");
    userPageSelect.innerHTML = "";

    for (let i = 0; i < response.totalPages; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.text = i + 1;
        userPageSelect.appendChild(option);
    }

    userPageSelect.value = response.number;
    userPageSelect.setAttribute("onchange", `listUsers(this.value)`);
}

/**
 * Navigates to the previous page in the user pagination.
 */
function previousUserPage() {
    const userPageSelect = document.getElementById("user-page-select");
    const currentPage = parseInt(userPageSelect.value);
    if (currentPage > 0) {
        userPageSelect.value = currentPage - 1;
        userPageSelect.dispatchEvent(new Event('change'));
    }
}

/**
 * Navigates to the next page in the user pagination.
 */
function nextUserPage() {
    const userPageSelect = document.getElementById("user-page-select");
    const currentPage = parseInt(userPageSelect.value);
    if (currentPage < userPageSelect.options.length - 1) {
        userPageSelect.value = currentPage + 1;
        userPageSelect.dispatchEvent(new Event('change'));
    }
}

/**
 * Displays the send message form in the UI.
 */
function toggleSendMessage() {
    hideAllSections();
    document.getElementById("send-message-section").style.display = "block";
}

/**
 * Displays the list users section in the UI and fetches the list of users.
 */
function toggleListUsers() {
    hideAllSections();
    document.getElementById("list-users-section").style.display = "block";
    listUsers();
}

/**
 * Displays the add user form in the UI.
 */
function toggleAddUser() {
    hideAllSections();
    document.getElementById("add-user-section").style.display = "block";
}

/**
 * Sends a message to the specified receiver.
 * Collects input from the form and sends a POST request to the API.
 */
function sendMessage() {
    const receiver = document.getElementById("message-receiver").value;
    const content = document.getElementById("message-content").value;

    if (!receiver || !content) {
        alert('All fields are required!');
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", `${apiUrl}/message`, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert("Message sent successfully");
                document.getElementById("message-receiver").value = "";
                document.getElementById("message-content").value = "";
                getOutbox();
            } else {
                handleUnauthorized(xhr);
                alert("Failed to send message.");
            }
        }
    };
    xhr.send(JSON.stringify({ receiver, content }));
}

/**
 * Searches for usernames based on the input query.
 */
function searchUsernames() {
    const query = document.getElementById("message-receiver").value;
    if (query.length === 0) {
        document.getElementById("username-suggestions").style.display = "none";
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("GET", `${apiUrl}/user/search?username=${query}`, true);
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status >= 200 && xhr.status < 300) {
            const users = JSON.parse(xhr.responseText);
            displayUsernameSuggestions(users);
        }
    };
    xhr.send();
}

/**
 * Displays username suggestions based on the search query.
 * @param {Array} users - The list of suggested usernames.
 */
function displayUsernameSuggestions(users) {
    const suggestions = document.getElementById("username-suggestions");
    suggestions.innerHTML = "";
    users.forEach(user => {
        const div = document.createElement("div");
        div.textContent = user;
        div.onclick = () => {
            document.getElementById("message-receiver").value = user;
            suggestions.style.display = "none";
        };
        suggestions.appendChild(div);
    });
    suggestions.style.display = "block";
}

/**
 * Adds a new user to the system.
 * Collects input from the form and sends a POST request to the API.
 */
function addUser() {
    const username = document.getElementById("add-user-username").value;
    const password = document.getElementById("add-user-password").value;
    const name = document.getElementById("add-user-name").value;
    const surname = document.getElementById("add-user-surname").value;
    const birthdate = document.getElementById("add-user-birthdate").value;
    const gender = document.getElementById("add-user-gender").value;
    const email = document.getElementById("add-user-email").value;
    const location = document.getElementById("add-user-location").value;
    const admin = document.getElementById("add-user-admin").checked;

    if (!username || !password || !name || !surname || !birthdate || !gender || !email || !location) {
        alert('All fields are required!');
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", `${apiUrl}/user`, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert("User added successfully");
                document.getElementById("add-user-username").value = "";
                document.getElementById("add-user-password").value = "";
                document.getElementById("add-user-name").value = "";
                document.getElementById("add-user-surname").value = "";
                document.getElementById("add-user-birthdate").value = "";
                document.getElementById("add-user-gender").value = "";
                document.getElementById("add-user-email").value = "";
                document.getElementById("add-user-location").value = "";
                document.getElementById("add-user-admin").checked = false;
                listUsers();
            } else {
                handleUnauthorized(xhr);
                alert("Failed to add user");
            }
        }
    };
    xhr.send(JSON.stringify({
        username, password, name, surname, birthdate, gender, email, location, admin
    }));
}

/**
 * Updates the selected user's information.
 * Collects input from the form and sends a PUT request to the API.
 */
function updateUser() {
    const selectedUser = document.querySelector("input[name='user-select']:checked");
    if (!selectedUser) {
        alert("Please select a user to update");
        return;
    }

    const username = selectedUser.value;
    const field = document.getElementById("user-field").value;
    const newValue = document.getElementById("user-new-value").value;

    if (!newValue) {
        alert('New value is required!');
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("PUT", `${apiUrl}/user/${username}?field=${field}&value=${newValue}`, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert("User updated successfully");
                listUsers();
            } else {
                handleUnauthorized(xhr);
                alert("Failed to update user");
            }
        }
    };
    const updateData = { [field]: newValue };
    xhr.send(JSON.stringify(updateData));
}

/**
 * Removes the selected user from the system.
 * Sends a DELETE request to the API.
 */
function removeUser() {
    const selectedUser = document.querySelector("input[name='user-select']:checked");
    if (!selectedUser) {
        alert("Please select a user to remove");
        return;
    }

    const username = selectedUser.value;

    const confirmed = confirm("Are you sure you want to remove this user?");
    if (!confirmed) {
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("DELETE", `${apiUrl}/user/${username}`, true);
    xhr.setRequestHeader("Authorization", authToken);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert("User removed successfully");
                listUsers();
            } else {
                handleUnauthorized(xhr);
                alert("Failed to remove user");
            }
        }
    };
    xhr.send();
}

/**
 * Parses a JSON Web Token (JWT) and returns the payload as a JSON object.
 * @param {string} token - The JWT to parse.
 * @returns {Object} The payload of the JWT.
 */
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}
