# Apexiz Weld Viewer

This repository now contains the full Android + web project structure for the Apexiz Weld Viewer.

## Project structure

- `index.html`
  Main web application source.
- `mobile-builder.css`
  Mobile and builder-specific styling.
- `mobile-builder.js`
  Mobile navigation helpers and runtime glue.
- `web/`
  Capacitor web bundle used by the Android wrapper.
- `android/`
  Native Android project for Capacitor builds.
- `scripts/`
  Local sync/build helper scripts.
- `package.json`
  Project scripts and Capacitor dependencies.
- `capacitor.config.json`
  Capacitor app configuration.
- `Logo.png`
  Shared logo asset.

## Common commands

```bash
npm run sync:web
npm run cap:copy
npm run cap:sync
```
