#ifndef CANCELLATION_H
#define CANCELLATION_H

#include <mutex>
#include <set>
#include <stdexcept>
#include <string>

namespace lsl {
class cancellable_object;

/// Exception class that indicates that an operation was performed on an registry that is being shut
/// down.
class shutdown_error : public std::runtime_error {
public:
	explicit shutdown_error(const std::string &msg) : std::runtime_error(msg) {}
	explicit shutdown_error(const char *msg) : std::runtime_error(msg) {}
};


/// A registry for objects that can be cancelled.
class cancellable_registry {
public:
	virtual ~cancellable_registry();

	/// Invoke cancel() on all currently registered objects.
	void cancel_all_registered();

	/// Invoke a cancel on all currently registered objects and prevent future object registration.
	void cancel_and_shutdown();

private:
	friend class cancellable_obj;

	/// Register a cancellable object.
	void register_cancellable(class cancellable_obj *o) {
		std::lock_guard<std::recursive_mutex> lock(state_mut_);
		if (shutdown_issued_)
			throw shutdown_error(
				"The registry has begun to shut down; no new registrations possible.");
		cancellables_.insert(o);
	}
	/// Unregister a cancellable object.
	void unregister_cancellable(class cancellable_obj *o) {
		std::lock_guard<std::recursive_mutex> lock(state_mut_);
		cancellables_.erase(o);
	}

	bool shutdown_issued_{false}; // whether a shutdown has been issued
	std::set<cancellable_obj *>
		cancellables_; // a set of objects that we have to cancel upon re-resolves & disengage
	std::recursive_mutex state_mut_; // mutex to protect the registry's state
};


/// A cancellable object.
class cancellable_obj {
public:
	/// Cancel method.
	virtual void cancel() = 0;

	/// Unregister at destruction.
	virtual ~cancellable_obj();

	/// Register at some registry.
	/// IMPORTANT: The registry must outlive the cancellable.
	void register_at(cancellable_registry *reg) {
		reg->register_cancellable(this);
		registered_at_.insert(reg);
	}

	/// IMPORTANT: If your registered cancel() operation may require some resources of your
	/// derived class(es), you must call unregister_from_all() *before* you destroy any of these
	/// resources...
	void unregister_from_all();

private:
	// set of registries where we are registered
	std::set<cancellable_registry *> registered_at_;
};

/// Cancel all registered objects.
inline void cancellable_registry::cancel_all_registered() {
	std::lock_guard<std::recursive_mutex> lock(state_mut_);
	std::set<cancellable_obj *> copy(cancellables_);
	for (auto *obj : copy)
		if (cancellables_.find(obj) != cancellables_.end()) obj->cancel();
}

/// Cancel and prevent future object registrations.
inline void cancellable_registry::cancel_and_shutdown() {
	std::lock_guard<std::recursive_mutex> lock(state_mut_);
	shutdown_issued_ = true;
	cancel_all_registered();
}

} // namespace lsl
#endif
