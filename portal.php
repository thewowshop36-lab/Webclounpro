<?php
// Denvork Portal PHP Entrypoint
// Redirects or serves the mobile web application while preserving referral queries (e.g., ref_id=Njk4MDc4)
$ref_id = isset($_GET['ref_id']) ? htmlspecialchars($_GET['ref_id'], ENT_QUOTES, 'UTF-8') : 'Njk4MDc4';
$query = $_SERVER['QUERY_STRING'] ? '?' . $_SERVER['QUERY_STRING'] : '?ref_id=' . $ref_id;
if (file_exists(__DIR__ . '/index.html')) {
    include __DIR__ . '/index.html';
} else {
    header("Location: /" . $query);
    exit;
}
?>
