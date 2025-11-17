import jsPDF from 'jspdf';
import * as XLSX from 'xlsx';

/**
 * Flattens the transaction list grouped by date into a single array
 */
const flattenTransactions = (transactionList) => {
    const allTransactions = [];
    Object.keys(transactionList).forEach((date) => {
        transactionList[date].forEach((transaction) => {
            allTransactions.push({
                ...transaction,
                date: date
            });
        });
    });
    return allTransactions;
};

/**
 * Formats date for display
 */
const formatDate = (dateString) => {
    if (["Today", "Yesterday"].includes(dateString)) {
        return dateString;
    }
    const date = new Date(dateString);
    const y = date.getFullYear();
    const m = date.toLocaleDateString('en-US', { month: 'long' });
    const d = date.getDate();
    return `${d} ${m} ${y}`;
};

/**
 * Exports transactions to PDF
 */
export const exportToPDF = (transactionList, filename = 'transactions') => {
    const transactions = flattenTransactions(transactionList);
    
    if (transactions.length === 0) {
        alert('No transactions to export');
        return;
    }

    const doc = new jsPDF();
    let yPos = 20;
    const pageHeight = doc.internal.pageSize.height;
    const margin = 20;
    const lineHeight = 7;

    // Title
    doc.setFontSize(16);
    doc.text('Transactions History', margin, yPos);
    yPos += 10;

    // Date and time
    doc.setFontSize(10);
    doc.text(`Generated on: ${new Date().toLocaleString()}`, margin, yPos);
    yPos += 10;

    // Summary
    const totalIncome = transactions
        .filter(t => t.transactionType === 2)
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);
    const totalExpense = transactions
        .filter(t => t.transactionType === 1)
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);
    const balance = totalIncome - totalExpense;

    doc.setFontSize(12);
    doc.text('Summary:', margin, yPos);
    yPos += 7;
    doc.setFontSize(10);
    doc.text(`Total Income: Rs. ${totalIncome.toFixed(2)}`, margin + 5, yPos);
    yPos += 7;
    doc.text(`Total Expense: Rs. ${totalExpense.toFixed(2)}`, margin + 5, yPos);
    yPos += 7;
    doc.text(`Balance: Rs. ${balance.toFixed(2)}`, margin + 5, yPos);
    yPos += 10;

    // Table header
    doc.setFontSize(10);
    doc.setFont(undefined, 'bold');
    doc.text('Date', margin, yPos);
    doc.text('Category', margin + 40, yPos);
    doc.text('Description', margin + 80, yPos);
    doc.text('Type', margin + 130, yPos);
    doc.text('Amount', margin + 160, yPos);
    yPos += 7;
    
    // Draw line
    doc.setLineWidth(0.5);
    doc.line(margin, yPos, 190, yPos);
    yPos += 5;

    // Transactions
    doc.setFont(undefined, 'normal');
    transactions.forEach((transaction) => {
        // Check if we need a new page
        if (yPos > pageHeight - 20) {
            doc.addPage();
            yPos = 20;
        }

        const date = formatDate(transaction.date);
        const category = transaction.categoryName || 'N/A';
        const description = transaction.description || 'N/A';
        const type = transaction.transactionType === 1 ? 'Expense' : 'Income';
        const amount = `Rs. ${parseFloat(transaction.amount || 0).toFixed(2)}`;

        doc.setFontSize(9);
        doc.text(date.substring(0, 15), margin, yPos);
        doc.text(category.substring(0, 20), margin + 40, yPos);
        doc.text(description.substring(0, 25), margin + 80, yPos);
        doc.text(type, margin + 130, yPos);
        doc.text(amount, margin + 160, yPos);
        yPos += lineHeight;
    });

    // Save the PDF
    doc.save(`${filename}_${new Date().toISOString().split('T')[0]}.pdf`);
};

/**
 * Exports transactions to Excel
 */
export const exportToExcel = (transactionList, filename = 'transactions') => {
    const transactions = flattenTransactions(transactionList);
    
    if (transactions.length === 0) {
        alert('No transactions to export');
        return;
    }

    // Prepare data for Excel
    const excelData = transactions.map((transaction) => ({
        'Date': formatDate(transaction.date),
        'Category': transaction.categoryName || 'N/A',
        'Description': transaction.description || 'N/A',
        'Type': transaction.transactionType === 1 ? 'Expense' : 'Income',
        'Amount': parseFloat(transaction.amount || 0).toFixed(2),
    }));

    // Calculate summary
    const totalIncome = transactions
        .filter(t => t.transactionType === 2)
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);
    const totalExpense = transactions
        .filter(t => t.transactionType === 1)
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);
    const balance = totalIncome - totalExpense;

    // Add summary rows
    excelData.push({});
    excelData.push({ 'Date': 'Summary', 'Category': '', 'Description': '', 'Type': '', 'Amount': '' });
    excelData.push({ 'Date': '', 'Category': 'Total Income', 'Description': '', 'Type': '', 'Amount': totalIncome.toFixed(2) });
    excelData.push({ 'Date': '', 'Category': 'Total Expense', 'Description': '', 'Type': '', 'Amount': totalExpense.toFixed(2) });
    excelData.push({ 'Date': '', 'Category': 'Balance', 'Description': '', 'Type': '', 'Amount': balance.toFixed(2) });

    // Create workbook and worksheet
    const ws = XLSX.utils.json_to_sheet(excelData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Transactions');

    // Set column widths
    ws['!cols'] = [
        { wch: 20 }, // Date
        { wch: 20 }, // Category
        { wch: 30 }, // Description
        { wch: 12 }, // Type
        { wch: 15 }, // Amount
    ];

    // Save the file
    XLSX.writeFile(wb, `${filename}_${new Date().toISOString().split('T')[0]}.xlsx`);
};

