# Changelog

All notable changes to LunarCore Fabric will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) (LC-VER-001).

## [1.0.0] - 2025-12-22

### Added
- **LifecycleManager** - Mod lifecycle callback system for init, client, server, and shutdown phases
- **Config** - JSON-based configuration system with type-safe getters and automatic defaults
  - Sensitive value redaction per LC-CFG-007
  - Support for string, int, boolean, and double types
- **LunarLogger** - Enhanced SLF4J wrapper with per-mod logger instances
  - Component naming following LC-LOG-005 convention (LunarCore.ModId)
  - Automatic sensitive data redaction per LC-LOG-007
  - Support for formatted messages
- **EventHelper** - Simplified Fabric event registration with batch support
  - Automatic error handling
  - Fluent API for multiple events
- **TaskScheduler** - Thread-safe task scheduling system
  - Delayed task execution
  - Repeating/periodic tasks
  - Task cancellation
  - Main thread execution guarantee
- **UpdateChecker** - Asynchronous version checking
  - GitHub Releases support
  - Custom JSON endpoint support
- **Error Hierarchy** - Comprehensive error handling per LC-ERR-001
  - LunarCoreError base class
  - ConfigurationError for config issues
  - ValidationError for validation failures
  - Error codes following LC_ERR_XXX format
- **ExampleMod** - Complete working example demonstrating all features
- Comprehensive documentation covering all components

### Dependencies
- Minecraft 1.21.11
- Fabric Loader >= 0.18.3
- Fabric API 0.140.2+1.21.11
- Java 21+

### Compliance
This release implements the following LunarCore Specification standards:
- LC-VER-001: Semantic Versioning
- LC-VER-003: Version declaration
- LC-VER-008: Changelog requirement
- LC-LOG-005: Component naming convention
- LC-LOG-007: Sensitive data redaction
- LC-CFG-007: Sensitive value handling
- LC-ERR-001: Error hierarchy
- LC-ERR-002: Error properties
- LC-ERR-003: Error codes
- LC-NAM-002: Casing standards
- LC-NAM-004: Function/method naming

### Design Principles
- ✅ Minimal and lightweight - Only essential utilities
- ✅ Fabric-native - Built entirely on Fabric APIs
- ✅ No forced mechanics - Zero gameplay changes, no UI, optional mixins
- ✅ Thread-safe - Safe scheduling and async operations
- ✅ Easy to use - Simple, intuitive API

[1.0.0]: https://github.com/LunarBit-dev/LunarCore-spec/releases/tag/v1.0.0

