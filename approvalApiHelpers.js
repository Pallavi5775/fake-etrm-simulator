// Example of updated approval API calls from frontend

const approveTradeWithAuth = async (tradeId) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  
  const res = await fetch(`http://localhost:8080/api/approvals/${tradeId}/approve`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-User-Role": user.role,        // User's role from login
      "X-User-Name": user.username     // User's username from login
    }
  });

  return res.json();
};

const rejectTradeWithAuth = async (tradeId, reason) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  
  const res = await fetch(`http://localhost:8080/api/approvals/${tradeId}/reject`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-User-Role": user.role,
      "X-User-Name": user.username
    },
    body: JSON.stringify({ reason })
  });

  return res.json();
};

// Usage in React component
export default function ApprovalPanel() {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  
  const handleApprove = async (tradeId) => {
    try {
      await approveTradeWithAuth(tradeId);
      alert("Trade approved successfully!");
    } catch (error) {
      if (error.message.includes("requires role")) {
        alert(`You don't have permission. Required role might be different from your role: ${user.role}`);
      }
    }
  };

  return (
    <div>
      <p>Logged in as: {user.username} ({user.role})</p>
      {/* Rest of component */}
    </div>
  );
}

export { approveTradeWithAuth, rejectTradeWithAuth };
