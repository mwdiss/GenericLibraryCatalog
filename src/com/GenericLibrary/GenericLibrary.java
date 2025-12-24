package com.GenericLibrary;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic Library Catalog System demonstrating flexible Generic implementation.
 * @author Malith Dissanayake
 */
public class GenericLibrary {

	/** Signal for operation cancellation by user request. */
	@SuppressWarnings("serial")
	static class OperationCanceledException extends Exception {}

	/**
	 * Entry point orchestrating the main application flow.
	 * @param applicationArguments command line arguments
	 */
	public static void main(String[] applicationArguments) {
		GenericCatalog<LibraryItem<?>> libraryCatalogSystem = new GenericCatalog<>();
		Scanner scannerInputSource = new Scanner(System.in);
		System.out.println("=== Generic Library Catalog System ===");
		System.out.println("Note: Enter '-1' at any prompt to cancel/return.");

		while (true) {
			try {
				String menuSelectionChoice = getValidatedInput(scannerInputSource,
						"\n[1. Add Item | 2. Remove Item | 3. View Catalog]\nChoice: ",
						_ -> true, ""); // Empty validation allows switch default to handle

				if (menuSelectionChoice.equals("-1")) break;

				switch (menuSelectionChoice) {
				case "1" -> handleItemAddition(libraryCatalogSystem, scannerInputSource);
				case "2" -> handleSearchRemoval(libraryCatalogSystem, scannerInputSource, true);
				case "3" -> handleSearchRemoval(libraryCatalogSystem, scannerInputSource, false);
				default -> System.out.println("Invalid selection. Please choose 1-3.");
				}
			} catch (OperationCanceledException e) {
				System.out.println(">> Operation Cancelled.");
			} catch (Exception e) {
				System.out.println(">> System Error: " + e.getMessage());
			}
		}
		scannerInputSource.close();
		System.out.println("Application Terminated.");
	}

	/**
	 * Manages adding new items with content and ID validation loops.
	 */
	private static void handleItemAddition(GenericCatalog<LibraryItem<?>> catalogReference,
			Scanner scannerReference) throws OperationCanceledException {
		while (true) {
			String typeSelectionInput = getValidatedInput(scannerReference,
					"\nItem Type (1=Book, 2=DVD, 3=Magazine): ",
					s -> s.matches("[1-3]"), "Enter 1, 2 or 3.");

			String titleInputString = getValidatedInput(scannerReference, "Item Title: ",
					s -> !s.isEmpty(), "Title cannot be empty.");

			String authorInputString = getValidatedInput(scannerReference,
					"Author (3+ letters): ",
					s -> s.length() >= 3 && s.matches(".*[a-zA-Z]+.*"),
					"Name must be 3+ characters.");

			Class<?> targetClassType = typeSelectionInput.equals("2") ? Dvd.class :
				typeSelectionInput.equals("1") ? Book.class : Magazine.class;

			if (catalogReference.isContentDuplicate(targetClassType, titleInputString, authorInputString)) {
				System.out.println(">> Duplicate Content exists. Restarting...");
				continue;
			}

			while (true) {
				System.out.println(">> Suggested ID: " + catalogReference.getNextIdSuggestion());
				String uniqueIdInputVal = getValidatedInput(scannerReference, "Unique ID: ",
						s -> !s.isEmpty(), "ID cannot be empty.");

				if (catalogReference.isIdOccupied(uniqueIdInputVal)) {
					System.out.println(">> Error: ID Taken."); continue;
				}
				if (targetClassType == Dvd.class && !uniqueIdInputVal.matches("\\d+")) {
					System.out.println(">> Error: DVD ID must be integer."); continue;
				}

				try {
					catalogReference.addItemToCatalog(targetClassType == Dvd.class
							? new Dvd(titleInputString, authorInputString, Integer.parseInt(uniqueIdInputVal))
									: targetClassType == Book.class
									? new Book(titleInputString, authorInputString, uniqueIdInputVal)
											: new Magazine(titleInputString, authorInputString, uniqueIdInputVal));

					System.out.println(">> Success: Item added.");
					return;
				} catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
			}
		}
	}

	/**
	 * Search and Removal logic with multiple matches handle.
	 */
	private static void handleSearchRemoval(GenericCatalog<LibraryItem<?>> catalogReference,
			Scanner scannerReference, boolean isRemoveOperation)
					throws OperationCanceledException {
		if (catalogReference.isCatalogEmpty()) {
			System.out.println("\nCatalog is EMPTY."); return;
		}

		String inputQueryString = getValidatedInput(scannerReference,
				isRemoveOperation ? "Enter ID/Title to Remove: " : "Search (-b/-d/-m): ", _->true,"");

		List<LibraryItem<?>> searchResultList = catalogReference.searchLibraryItems(inputQueryString);

		if (searchResultList.isEmpty()) {
			System.out.println("No matching items found.");
			return;
		}

		// View Logic
		if (!isRemoveOperation) {
			System.out.println("Found " + searchResultList.size() + " matches:");
			searchResultList.forEach(System.out::println);
			return;
		}

		// Removal Logic
		LibraryItem<?> targetItemToRemove = null;
		if (searchResultList.size() == 1) {
			targetItemToRemove = searchResultList.get(0);
		} else {
			System.out.println("Found " + searchResultList.size() + " matches. Select one to remove:");
			for (int i = 0; i < searchResultList.size(); i++) {
				System.out.printf("%d. %s%n", i + 1, searchResultList.get(i));
			}

			String selectionIndexStr = getValidatedInput(scannerReference,
					"Enter # to delete: ",
					s -> s.matches("\\d+") && Integer.parseInt(s) > 0
					&& Integer.parseInt(s) <= searchResultList.size(),
					"Invalid Number.");

			targetItemToRemove = searchResultList.get(Integer.parseInt(selectionIndexStr) - 1);
		}

		if (targetItemToRemove != null) {
			catalogReference.removeItemInstance(targetItemToRemove);
			System.out.println(">> Removed: " + targetItemToRemove);
		}
	}

	private static String getValidatedInput(Scanner scannerRef, String messagePrompt,
			Predicate<String> validatorLogic, String errorTextMsg)
					throws OperationCanceledException {
		while (true) {
			System.out.print(messagePrompt);
			String inputLineString = scannerRef.nextLine().trim();
			if (inputLineString.equals("-1")) throw new OperationCanceledException();
			if (validatorLogic.test(inputLineString)) return inputLineString;
			System.out.println(">> Error: " + errorTextMsg);
		}
	}
}

/**
 * Generic management class for library entities.
 * @param <T> specific library item type
 */
class GenericCatalog<T extends LibraryItem<?>> {
	private final List<T> internalStorageList = new ArrayList<>();

	public void addItemToCatalog(T libraryItemEntity) { internalStorageList.add(libraryItemEntity); }
	public void removeItemInstance(T libraryItemEntity) { internalStorageList.remove(libraryItemEntity); }
	public boolean isCatalogEmpty() { return internalStorageList.isEmpty(); }

	public boolean isContentDuplicate(Class<?> itemClass, String titleStr, String authorStr) {
		return internalStorageList.stream().anyMatch(i -> i.getClass().equals(itemClass) &&
				i.getItemTitleStr().equalsIgnoreCase(titleStr) && i.getItemAuthorStr().equalsIgnoreCase(authorStr));
	}

	public boolean isIdOccupied(String idStringVal) {
		return internalStorageList.stream().anyMatch(i -> String.valueOf(i.getItemIdentVal()).equals(idStringVal));
	}

	public String getNextIdSuggestion() {
		return String.valueOf(internalStorageList.stream().map(i -> String.valueOf(i.getItemIdentVal()))
				.filter(s -> s.matches("\\d+")).mapToInt(Integer::parseInt).max().orElse(0) + 1);
	}

	public List<T> searchLibraryItems(String searchQueryVal) {
		String cleanQueryLower = searchQueryVal.toLowerCase().trim();
		Class<?> filterType = cleanQueryLower.contains("-b") ? Book.class :
			cleanQueryLower.contains("-d") ? Dvd.class :
				cleanQueryLower.contains("-m") ? Magazine.class : null;

		String termStr = cleanQueryLower.replaceAll("-[bdm]", "").trim();
		return internalStorageList.stream().filter(item -> {
			boolean typeMatch = filterType == null || item.getClass().equals(filterType);
			if (!typeMatch) return false;
			String idStr = String.valueOf(item.getItemIdentVal()).toLowerCase();
			return termStr.isEmpty() || idStr.equals(termStr) || idStr.contains(termStr) ||
					item.getItemTitleStr().toLowerCase().contains(termStr) ||
					item.getItemAuthorStr().toLowerCase().contains(termStr);
		}).collect(Collectors.toList());
	}
}

/**
 * Abstract Generic Base Class.
 * @param <ID> Flexible Data Type
 */
abstract class LibraryItem<ID> {
	private final String itemTitleString;
	private final String itemAuthorString;
	private final ID itemIdentifierVal;

	public LibraryItem(String t, String a, ID i) {
		itemTitleString = t; itemAuthorString = a; itemIdentifierVal = i;
	}
	public String getItemTitleStr() { return itemTitleString; }
	public String getItemAuthorStr() { return itemAuthorString; }
	public ID getItemIdentVal() { return itemIdentifierVal; }

	@Override public String toString() {
		return String.format("[%-8s] ID: %-5s | Title: %-20s | Auth: %s",
				getClass().getSimpleName(), itemIdentifierVal, itemTitleString, itemAuthorString);
	}
}

class Book extends LibraryItem<String> { public Book(String t, String a, String i){super(t,a,i);} }
class Dvd extends LibraryItem<Integer> { public Dvd(String t, String a, Integer i){super(t,a,i);} }
class Magazine extends LibraryItem<String> { public Magazine(String t, String a, String i){super(t,a,i);} }