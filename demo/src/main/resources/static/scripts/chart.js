fetch("/api/dashboard/stats")
  .then(res => res.json())
  .then(data => {
    // Example for traffic volume chart
    new Chart(document.getElementById('trafficVolumeChart'), {
      type: 'line',
      data: {
        labels: Object.keys(data.trafficVolume),
        datasets: [{
          label: 'Traffic Volume',
          data: Object.values(data.trafficVolume),
          borderColor: 'rgba(122, 12, 30, 1)',
          fill: true,
          tension: 0.3
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });

    // Top Blocked Ports
    new Chart(document.getElementById('blockedPortsChart'), {
      type: 'bar',
      data: {
        labels: data.blockedPorts.map(p => `${p.port}`),
        datasets: [{
          label: 'Blocked Attempts',
          data: data.blockedPorts.map(p => p.count),
          backgroundColor: '#7a0c1e'
        }]
      },
      options: {
        plugins: { title: { display: true, text: 'Top Blocked Ports' } },
        maintainAspectRatio: false,
        indexAxis: 'y',
        responsive: true
      }
    });

    // Protocol Usage
    new Chart(document.getElementById('protocolUsageChart'), {
      type: 'doughnut',
      data: {
        labels: Object.keys(data.protocolUsage),
        datasets: [{
          label: 'Protocols',
          data: Object.values(data.protocolUsage),
          backgroundColor: ['#7a0c1e', '#a02a2a', '#d9534f', '#f0ad4e']
        }]
      },
      options: {
        plugins: { title: { display: true, text: 'Protocol Usage Distribution' } },
        maintainAspectRatio: false,
        responsive: true
      }
    });

    // Top Source IPs
    new Chart(document.getElementById('topSourceIPsChart'), {
      type: 'bar',
      data: {
        labels: data.topSourceIps.map(i => i.ip),
        datasets: [{
          label: 'Blocked Requests',
          data: data.topSourceIps.map(i => i.count),
          backgroundColor: '#a02a2a'
        }]
      },
      options: {
        plugins: { title: { display: true, text: 'Top Source IPs (Blocked)' } },
        maintainAspectRatio: false,
        responsive: true
      }
    });

    // Allowed vs Denied
    new Chart(document.getElementById('allowedDeniedChart'), {
      type: 'bar',
      data: {
        labels: ['Traffic'],
        datasets: [
          {
            label: 'Allowed',
            data: [data.trafficSplit.ALLOW || 0],
            backgroundColor: '#5cb85c'
          },
          {
            label: 'Denied',
            data: [data.trafficSplit.DENY || 0],
            backgroundColor: '#d9534f'
          }
        ]
      },
      options: {
        indexAxis: 'y',
        plugins: { title: { display: true, text: 'Allowed vs. Denied Traffic' } },
        maintainAspectRatio: false,
        responsive: true,
        scales: {
          x: { stacked: true },
          y: { stacked: true }
        }
      }
    });
});