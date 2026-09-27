const list = document.querySelector('#claims-list');
const form = document.querySelector('#claim-form');
const message = document.querySelector('#form-message');
const filter = document.querySelector('#status-filter');

const money = new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' });

function showMessage(text, isError = false) { message.textContent = text; message.classList.toggle('error', isError); }

async function request(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.message || `Request failed (${response.status})`);
  }
  return response.status === 204 ? null : response.json();
}

function renderClaim(claim) {
  const node = document.querySelector('#claim-template').content.cloneNode(true);
  node.querySelector('h3').textContent = `${claim.claimNumber} · ${claim.claimantName}`;
  const badge = node.querySelector('.status');
  badge.textContent = claim.status; badge.classList.add(claim.status);
  node.querySelector('.meta').textContent = `${claim.type} · Policy ${claim.policyNumber}`;
  node.querySelector('.description').textContent = claim.description || 'No description supplied.';
  node.querySelector('.amount').textContent = money.format(claim.amount);
  node.querySelector('.created').textContent = `Created ${new Date(claim.createdAt).toLocaleDateString()}`;
  const actions = node.querySelector('.claim-actions');
  if (claim.status === 'SUBMITTED') {
    const approve = document.createElement('button'); approve.className = 'action-button'; approve.textContent = 'Approve'; approve.onclick = () => changeStatus(claim.id, 'approve');
    const reject = document.createElement('button'); reject.className = 'action-button reject'; reject.textContent = 'Reject'; reject.onclick = () => changeStatus(claim.id, 'reject');
    actions.append(approve, reject);
  }
  return node;
}

async function loadClaims() {
  list.innerHTML = '<p class="empty">Loading claims…</p>';
  try {
    const query = filter.value ? `?status=${filter.value}` : '';
    const claims = await request(`/api/claims${query}`);
    list.replaceChildren();
    if (!claims.length) { list.innerHTML = '<p class="empty">No claims found. Submit the first one above.</p>'; return; }
    claims.forEach(claim => list.append(renderClaim(claim)));
  } catch (error) { list.innerHTML = `<p class="empty">Could not load claims: ${error.message}</p>`; }
}

async function changeStatus(id, action) {
  try {
    let options = { method: 'POST' };
    if (action === 'reject') {
      const reason = window.prompt('Why is this claim being rejected?');
      if (reason === null) return;
      options = { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ reason }) };
    }
    await request(`/api/claims/${id}/${action}`, options);
    await loadClaims();
  } catch (error) { window.alert(error.message); }
}

form.addEventListener('submit', async event => {
  event.preventDefault(); showMessage('Submitting…');
  const values = Object.fromEntries(new FormData(form)); values.amount = Number(values.amount);
  try { await request('/api/claims', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(values) }); form.reset(); showMessage('Claim submitted successfully.'); await loadClaims(); }
  catch (error) { showMessage(error.message, true); }
});
document.querySelector('#refresh-button').addEventListener('click', loadClaims);
filter.addEventListener('change', loadClaims);
loadClaims();
