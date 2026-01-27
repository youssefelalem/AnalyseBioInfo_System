document.getElementById('fastaFile').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(e) {
        const content = e.target.result;
        
        // Extraction FASTA : 
        // On sépare les lignes, on enlève celles qui commencent par '>'
        // et on recolle tout sans espaces ni retours à la ligne.
        const lines = content.split('\n');
        const sequence = lines
            .filter(line => !line.trim().startsWith('>'))
            .map(line => line.trim().toUpperCase())
            .join('')
            .replace(/[^ATGC]/g, ''); // Ne garde que les vrais nucléotides

        document.getElementById('adnText').value = sequence;
    };
    reader.readAsText(file);
});

document.getElementById('analyseForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const formData = new FormData(this);
    const params = new URLSearchParams(formData);
    const resultDiv = document.getElementById('result-container');
    const verdictText = document.getElementById('verdict-text');

    fetch(	'./analyser', { 
	    method: 'POST',
	    headers: {
	        'Content-Type': 'application/x-www-form-urlencoded',
	    },
	    body: params
	})
    .then(response => response.text())
    .then(data => {
        resultDiv.style.display = 'block';
        verdictText.innerText = data;
        
        if(data.includes('SAIN')) verdictText.style.color = '#27ae60';
        else if(data.includes('MALADE')) verdictText.style.color = '#c0392b';
        else verdictText.style.color = '#d35400';
    })
    .catch(err => alert("Erreur serveur : " + err));
});